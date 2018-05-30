package it.scoppelletti.spaceship.sample.widget

import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import it.scoppelletti.spaceship.sample.ItemTabFragment
import it.scoppelletti.spaceship.sample.R
import it.scoppelletti.spaceship.sample.StubFragment
import it.scoppelletti.spaceship.widget.FragmentPagerAdapterEx

class ItemPagerAdapter(
        private val context: Context,
        fragmentMgr: FragmentManager
) : FragmentPagerAdapterEx(fragmentMgr) {

    override fun getCount(): Int = 3

    override fun getItem(position: Int): Fragment =
        when (position) {
            0 -> ItemTabFragment.newInstance()
            1 -> StubFragment.newInstance(R.string.lbl_tab2)
            2 -> StubFragment.newInstance(R.string.lbl_tab3)
            else -> throw IllegalArgumentException("Invalid position.")
        }

    override fun getPageTitle(position: Int): CharSequence? =
        context.getString(when (position) {
            0 -> R.string.lbl_tab1
            1 -> R.string.lbl_tab2
            2 -> R.string.lbl_tab3
            else -> throw IllegalArgumentException("Invalid position.")
        })
}