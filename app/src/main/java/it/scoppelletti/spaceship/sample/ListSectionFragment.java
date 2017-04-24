package it.scoppelletti.spaceship.sample;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;
import io.reactivex.schedulers.Schedulers;
import lombok.extern.slf4j.Slf4j;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import it.scoppelletti.spaceship.ExceptionEvent;
import it.scoppelletti.spaceship.rx.SingleCoordinator;
import it.scoppelletti.spaceship.rx.SingleObserverFactory;
import it.scoppelletti.spaceship.sample.data.DataForm;
import it.scoppelletti.spaceship.sample.data.DataProvider;
import it.scoppelletti.spaceship.sample.widget.DataListAdapter;

@Slf4j
public final class ListSectionFragment extends Fragment {
    public static final String TAG = MainApp.TAG_LISTSECTION;
    private int myPos;
    private List<DataForm> myList;
    private DataListAdapter myAdapter;
    private RecyclerView myListView;
    private LinearLayoutManager myListLayout;
    private CompositeDisposable myDisposables;

    @NonNull
    public static ListSectionFragment newInstance() {
        ListSectionFragment fragment;

        fragment = new ListSectionFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list_fragment, container,
                false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        View view;
        Context ctx;
        RecyclerView.ItemDecoration itemDeco;

        super.onActivityCreated(savedInstanceState);
        myDisposables = new CompositeDisposable();

        if (savedInstanceState == null) {
            myPos = getActivity().getIntent().getIntExtra(MainApp.PROP_LISTPOS,
                    RecyclerView.NO_POSITION);
        } else {
            myPos = savedInstanceState.getInt(MainApp.PROP_LISTPOS,
                    RecyclerView.NO_POSITION);
        }

        view = getView();
        ctx = getActivity();
        myListView = (RecyclerView) view.findViewById(R.id.list_view);
        myListLayout = new LinearLayoutManager(ctx);
        myListView.setLayoutManager(myListLayout);
        itemDeco = new DividerItemDecoration(ctx,
                myListLayout.getOrientation());
        myListView.addItemDecoration(itemDeco);

        myList = new ArrayList<>();
        myAdapter = new DataListAdapter();
        myAdapter.changeData(myList);
        myListView.setAdapter(myAdapter);

        listData();
    }

    private void listData() {
        Disposable connection;
        Single<List<DataForm>> lister;
        SingleCoordinator<List<DataForm>> coordinator;

        coordinator = DrawerActivityData.getInstance(getActivity()).getLister();
        if (coordinator.isRunning()) {
            return;
        }

        lister = DataProvider.list(getContext());
        connection = coordinator.connect(
                lister.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread()));
        myDisposables.add(connection);
    }

    @Override
    public void onResume() {
        Disposable subscription;
        SingleCoordinator<List<DataForm>> coordinator;

        super.onResume();
        EventBus.getDefault().register(this);

        coordinator = DrawerActivityData.getInstance(getActivity()).getLister();
        subscription = coordinator.subscribe(
                new SingleObserverFactory<List<DataForm>>() {

                    @NonNull
                    @Override
                    public DisposableSingleObserver<List<DataForm>> create() {
                        return new ListSectionFragment.ListObserver();
                    }
        });

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
        super.onSaveInstanceState(outState);

        if (myPos != RecyclerView.NO_POSITION) {
            outState.putInt(MainApp.PROP_LISTPOS, myPos);
        } else if (myListLayout != null) {
            outState.putInt(MainApp.PROP_LISTPOS,
                    myListLayout.findFirstCompletelyVisibleItemPosition());
        }
    }

    @Subscribe
    public void onDataNew(@NonNull DataNewEvent event) {
        Intent intent;

        intent = new Intent(getContext(), TabbedActivity.class);
        intent.putExtra(MainApp.PROP_SECTION, R.id.cmd_listSection);

        if (myListLayout != null) {
            intent.putExtra(MainApp.PROP_LISTPOS,
                    myListLayout.findFirstCompletelyVisibleItemPosition());
        }

        startActivity(intent);
    }

    @Subscribe
    public void onDataEdit(@NonNull DataEditEvent event) {
        Intent intent;

        intent = new Intent(getContext(), TabbedActivity.class);
        intent.putExtra(MainApp.PROP_SECTION, R.id.cmd_listSection);
        intent.putExtra(MainApp.PROP_DATAID, event.getId());

        if (myListLayout != null) {
            intent.putExtra(MainApp.PROP_LISTPOS,
                    myListLayout.findFirstCompletelyVisibleItemPosition());
        }

        startActivity(intent);
    }

    private final class ListObserver extends
            DisposableSingleObserver<List<DataForm>> {

        @Override
        protected void onStart() {
            EventBus.getDefault().post(DataAccessEvent.getInstance());
        }

        @Override
        public void onSuccess(@NonNull List<DataForm> list) {
            myList = list;
            myAdapter.changeData(myList);
            if (myPos != RecyclerView.NO_POSITION) {
                myListView.scrollToPosition(myPos);
                myPos = RecyclerView.NO_POSITION;
            }

            EventBus.getDefault().post(DataReadyEvent.getInstance());
        }

        @Override
        public void onError(@NonNull Throwable ex) {
            myLogger.error("Failed to list.", ex);
            EventBus.getDefault().post(new ExceptionEvent(ex)
                    .title(R.string.it_scoppelletti_cmd_list)
                    .requestCode(R.id.cmd_exit));
        }
    }
}
