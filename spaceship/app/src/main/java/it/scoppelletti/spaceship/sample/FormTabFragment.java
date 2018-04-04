package it.scoppelletti.spaceship.sample;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.TextView;
import io.reactivex.Completable;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableCompletableObserver;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.app.AppExt;
import it.scoppelletti.spaceship.app.ConfirmDialogFragment;
import it.scoppelletti.spaceship.app.DialogCloseEvent;
import it.scoppelletti.spaceship.rx.CompletableCoordinator;
import it.scoppelletti.spaceship.rx.CompleteEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.rx.StartEvent;
import it.scoppelletti.spaceship.sample.data.DataViewModel;
import it.scoppelletti.spaceship.sample.data.DataProvider;
import it.scoppelletti.spaceship.sample.databinding.FormFragmentBinding;
import it.scoppelletti.spaceship.widget.SnackbarEvent;

@Slf4j
public final class FormTabFragment extends Fragment implements
        TextView.OnEditorActionListener {
    public static final String TAG = MainApp.TAG_FORMTAB;
    private static final String PROP_FORM = "1";
    private FormFragmentBinding myBinding;
    private CompositeDisposable myDisposables;

    public FormTabFragment() {
        setHasOptionsMenu(true);
    }

    @NonNull
    public static FormTabFragment newInstance(long id) {
        Bundle args;
        FormTabFragment fragment;

        args = new Bundle();
        args.putLong(MainApp.PROP_DATAID, id);

        fragment = new FormTabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        myBinding = FormFragmentBinding.inflate(inflater, container, false);
        return myBinding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        long dataId;
        Bundle args;
        DataViewModel form;

        super.onActivityCreated(savedInstanceState);
        myDisposables = new CompositeDisposable();

        if (savedInstanceState == null) {
            args = getArguments();
            dataId = args.getLong(MainApp.PROP_DATAID, -1);
            if (dataId < 0) {
                form = new DataViewModel();
                form.setNewData(true);
            } else {
                form = null;
            }
        } else {
            form = savedInstanceState.getParcelable(FormTabFragment.PROP_FORM);
        }

        if (form != null) {
            myBinding.setModel(form);
        }

        myBinding.txtName.setOnEditorActionListener(this);
        myBinding.txtDesc.setOnEditorActionListener(this);
    }

    private void readData(TabbedActivityData fragment) {
        long dataId;
        Bundle args;
        Disposable connection;
        Single<DataViewModel> reader;
        SingleCoordinator<DataViewModel> coordinator;

        coordinator = fragment.getReader();
        if (coordinator.isRunning()) {
            return;
        }

        args = getArguments();
        dataId = args.getLong(MainApp.PROP_DATAID, -1);
        reader = DataProvider.read(getActivity(), dataId);
        connection = coordinator.connect(reader.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
        myDisposables.add(connection);
    }

    @Override
    public void onResume() {
        Disposable subscription;
        TabbedActivityData fragment;
        CompletableCoordinator completableCoordinator;
        SingleCoordinator<DataViewModel> singleCoordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        fragment = TabbedActivityData.getInstance(getActivity());

        if (myBinding != null && myBinding.getModel() == null) {
            readData(fragment);
        }

        singleCoordinator = fragment.getCreator();
        subscription = singleCoordinator.subscribe(() ->
                new FormTabFragment.CreateObserver());
        myDisposables.add(subscription);

        singleCoordinator = fragment.getReader();
        subscription = singleCoordinator.subscribe(() ->
                new FormTabFragment.ReadObserver());
        myDisposables.add(subscription);

        singleCoordinator = fragment.getUpdater();
        subscription = singleCoordinator.subscribe(() ->
                new FormTabFragment.UpdateObserver());
        myDisposables.add(subscription);

        completableCoordinator = fragment.getDeleter();
        subscription = completableCoordinator.subscribe(() ->
                new FormTabFragment.DeleteObserver());
          myDisposables.add(subscription);
    }

    @Override
    public void onPause() {
        myDisposables.dispose();
        myDisposables = new CompositeDisposable();

        EventBus.getDefault().unregister(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        DataViewModel form;

        super.onSaveInstanceState(outState);

        if (myBinding != null) {
            form = myBinding.getModel();
            if (form != null) {
                outState.putParcelable(FormTabFragment.PROP_FORM, form);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.form, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        MenuItem menuItem;
        DataViewModel form;

        super.onPrepareOptionsMenu(menu);

        form = (myBinding == null) ? null : myBinding.getModel();
        menuItem = menu.findItem(R.id.cmd_ok);
        menuItem.setEnabled(form != null && (form.isNewData() ||
                form.isChanged()));

        menuItem = menu.findItem(R.id.cmd_delete);
        menuItem.setEnabled(form != null && !form.isNewData() &&
                !form.isChanged());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
        case R.id.cmd_ok:
            onDoneClick();
            return true;

        case R.id.cmd_delete:
            onDeleteClick();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onEditorAction(@NonNull TextView v, int actionId,
            @Nullable KeyEvent event) {
        switch (actionId) {
        case EditorInfo.IME_ACTION_DONE:
            onDoneClick();
            return true;
        }

        return false;
    }

    private void onDoneClick() {
        boolean valid;
        DataViewModel data;
        Disposable connection;
        Single<DataViewModel> saver;
        TabbedActivityData fragment;
        SingleCoordinator<DataViewModel> coordinator;

        AppExt.hideSoftKeyboard(getActivity());

        data = myBinding.getModel();

        valid = data.getNameValidator().validate();
        valid = valid && data.getDescValidator().validate();
        if (!valid) {
            return;
        }

        fragment = TabbedActivityData.getInstance(getActivity());
        coordinator = (data.isNewData()) ? fragment.getCreator() :
            fragment.getUpdater();
        if (coordinator.isRunning()) {
            return;
        }

        saver = (data.isNewData()) ? DataProvider.create(data) :
                DataProvider.update(data);
        connection = coordinator.connect(saver.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));
        myDisposables.add(connection);
    }

    private void onDeleteClick() {
        AppExt.hideSoftKeyboard(getActivity());
        new ConfirmDialogFragment.Builder(getActivity(),
                R.string.msg_dataDeleting)
                .title(R.string.it_scoppelletti_cmd_delete)
                .affirmativeActionText(R.string.it_scoppelletti_cmd_delete)
                .closeEvent(new DialogCloseEvent(R.id.cmd_delete))
                .show();
    }

    @Subscribe
    public void onDelete(@NonNull DialogCloseEvent event) {
        DataViewModel data;
        Disposable connection;
        Completable deleter;
        CompletableCoordinator coordinator;

        if (event.getRequestCode() != R.id.cmd_delete ||
                event.getResult() != DialogInterface.BUTTON_POSITIVE) {
            return;
        }

        coordinator = TabbedActivityData.getInstance(
                getActivity()).getDeleter();
        if (coordinator.isRunning()) {
            return;
        }

        data = myBinding.getModel();
        deleter = DataProvider.delete(data.getId());
        connection = coordinator.connect(deleter.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread()));
        myDisposables.add(connection);
    }

    private final class CreateObserver extends
            DisposableSingleObserver<DataViewModel> {

        @Override
        protected void onStart() {
            EventBus.getDefault().post(StartEvent.getInstance());
        }

        @Override
        public void onSuccess(@NonNull DataViewModel data) {
            myBinding.setModel(data);
            EventBus.getDefault().post(new DataCreateEvent(data.getId()));
        }

        @Override
        public void onError(@NonNull Throwable ex) {
            myLogger.error("Failed to create.", ex);
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_save));
        }
    }

    private final class ReadObserver extends
            DisposableSingleObserver<DataViewModel> {

        @Override
        protected void onStart() {
            EventBus.getDefault().post(StartEvent.getInstance());
        }

        @Override
        public void onSuccess(@NonNull DataViewModel data) {
            myBinding.setModel(data);
            EventBus.getDefault().post(CompleteEvent.getInstance());
        }

        @Override
        public void onError(@NonNull Throwable ex) {
            myLogger.error("Failed to read.", ex);
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_open)
                    .requestCode(R.id.cmd_exit));
        }
    }

    private final class UpdateObserver extends
            DisposableSingleObserver<DataViewModel> {

        @Override
        protected void onStart() {
            EventBus.getDefault().post(StartEvent.getInstance());
        }

        @Override
        public void onSuccess(@NonNull DataViewModel data) {
            myBinding.setModel(data);
            EventBus.getDefault().post(new SnackbarEvent(
                    R.string.msg_dataUpdated, Snackbar.LENGTH_SHORT));
        }

        @Override
        public void onError(@NonNull Throwable ex) {
            myLogger.error("Failed to update.", ex);
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_update));
        }
    }

    private final class DeleteObserver extends DisposableCompletableObserver {

        @Override
        protected void onStart() {
            EventBus.getDefault().post(StartEvent.getInstance());
        }

        @Override
        public void onComplete() {
            EventBus.getDefault().post(DataDeleteEvent.getInstance());
        }

        @Override
        public void onError(@NonNull Throwable ex) {
            myLogger.error("Failed to delete.", ex);
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_delete));
        }
    }
}
