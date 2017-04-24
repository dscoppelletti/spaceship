package it.scoppelletti.spaceship.sample.data;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import android.content.Context;
import android.support.annotation.NonNull;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Action;
import io.reactivex.functions.Function;
import lombok.extern.slf4j.Slf4j;
import it.scoppelletti.spaceship.sample.R;

@Slf4j
public final class DataProvider {
    private static final int DELAY = 10000;
    private static final int LIST_SIZE = 50;
    private static final AtomicLong myIdGenerator =
            new AtomicLong(DataProvider.LIST_SIZE);

    private DataProvider() {
    }

    public static Single<DataForm> create(@NonNull DataForm data) {
        if (data == null) {
            throw new NullPointerException("Argument data is null.");
        }

        return Single.fromCallable(new DataProvider.Creator(data))
                .delay(DataProvider.DELAY, TimeUnit.MILLISECONDS);
    }

    public static Single<DataForm> read(@NonNull Context ctx, long id) {
        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }

        return Single.just((int) id)
                .delay(DataProvider.DELAY, TimeUnit.MILLISECONDS)
                .map(new DataProvider.Reader(ctx.getApplicationContext()));
    }

    public static Single<DataForm> update(@NonNull DataForm data) {
        if (data == null) {
            throw new NullPointerException("Argument data is null.");
        }

        return Single.fromCallable(new DataProvider.Updater(data))
                .delay(DataProvider.DELAY, TimeUnit.MILLISECONDS);
    }

    public static Completable delete(long id) {
        return Completable.fromAction(new DataProvider.Deleter(id))
                .delay(DataProvider.DELAY, TimeUnit.MILLISECONDS);
    }

    public static Single<List<DataForm>> list(@NonNull Context ctx) {
        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }

        return Observable.range(1, DataProvider.LIST_SIZE)
                .delay(DataProvider.DELAY, TimeUnit.MILLISECONDS)
                .map(new DataProvider.Reader(ctx.getApplicationContext()))
                .toList();
    }

    private static final class Creator implements Callable<DataForm> {
        private DataForm myData;

        Creator(DataForm data) {
            myData = data;
        }

        @Override
        public DataForm call() throws Exception {
            DataForm form;

            form = new DataForm();
            form.setId(myIdGenerator.incrementAndGet());
            form.setName(myData.getName());
            form.setDesc(myData.getDesc());
            form.setNewData(false);
            form.setChanged(false);
            myLogger.debug("Create {}.", form.getId());

            return form;
        }
    }

    private static final class Reader implements Function<Integer, DataForm> {
        private final Context myCtx;

        Reader(Context ctx) {
            myCtx = ctx;
        }

        @Override
        public DataForm apply(Integer id) throws Exception {
            DataForm form;

            myLogger.trace("Read {}.", id);
            form = new DataForm();
            form.setId(id);
            form.setName(myCtx.getResources().getString(R.string.fmt_name, id));
            form.setDesc(myCtx.getResources().getString(R.string.fmt_desc, id));
            form.setNewData(false);
            form.setChanged(false);

            return form;
        }
    }

    private static final class Updater implements Callable<DataForm> {
        private DataForm myData;

        Updater(DataForm data) {
            myData = data;
        }

        @Override
        public DataForm call() throws Exception {
            DataForm form;

            form = new DataForm();
            form.setId(myData.getId());
            form.setName(myData.getName());
            form.setDesc(myData.getDesc());
            form.setNewData(false);
            form.setChanged(false);
            myLogger.debug("Update {}.", form.getId());

            return form;
        }
    }

    private static final class Deleter implements Action {
        private final long myId;

        Deleter(long id) {
            myId = id;
        }

        @Override
        public void run() throws Exception {
            myLogger.debug("Delete {}.", myId);
        }
    }
}
