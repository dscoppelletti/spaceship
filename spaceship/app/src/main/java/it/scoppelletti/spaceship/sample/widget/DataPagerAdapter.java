package it.scoppelletti.spaceship.sample.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import it.scoppelletti.spaceship.sample.R;
import it.scoppelletti.spaceship.sample.FormTabFragment;
import it.scoppelletti.spaceship.widget.FragmentPagerAdapterEx;

public final class DataPagerAdapter extends FragmentPagerAdapterEx {
    private final Context myCtx;
    private final long myDataId;

    public DataPagerAdapter(@NonNull Context ctx,
            @NonNull FragmentManager fragmentMgr, long dataId) {
        super(fragmentMgr);

        if (ctx == null) {
            throw new NullPointerException("Argument ctx is null.");
        }
        if (fragmentMgr == null) {
            throw new NullPointerException("Argument fragmentMgr is null.");
        }

        myCtx = ctx.getApplicationContext();
        myDataId = dataId;
    }

    @Override
    public int getCount() {
        return 1;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return FormTabFragment.newInstance(myDataId);
    }

    @NonNull
    @Override
    public CharSequence getPageTitle(int position) {
        return myCtx.getString(R.string.lbl_formTab);
    }
}
