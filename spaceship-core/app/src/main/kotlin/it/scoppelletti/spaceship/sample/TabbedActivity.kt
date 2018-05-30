package it.scoppelletti.spaceship.sample

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProvider
import android.arch.lifecycle.ViewModelProviders
import android.content.DialogInterface
import android.databinding.Observable
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBar
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import it.scoppelletti.spaceship.app.ConfirmDialogFragment
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.TitleAdapter
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.sample.viewmodel.ItemState
import it.scoppelletti.spaceship.sample.viewmodel.ItemViewModel
import it.scoppelletti.spaceship.sample.widget.ItemPagerAdapter
import kotlinx.android.synthetic.main.tabbed_activity.*
import javax.inject.Inject

class TabbedActivity : AppCompatActivity(),
        HasSupportFragmentInjector,
        OnDialogResultListener,
        OnItemActionListener {

    @Inject
    lateinit var fragmentDispatchingAndroidInjector:
            DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var titleAdapter: TitleAdapter
    private lateinit var viewModel: ItemViewModel

    private val onPropertyChanged = object :
            Observable.OnPropertyChangedCallback() {

        override fun onPropertyChanged(sender: Observable?, propertyId: Int) {
            when (propertyId) {
                BR.changed -> invalidateOptionsMenu()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        val actionBar: ActionBar

        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbed_activity)

        setSupportActionBar(toolbar)
        actionBar = supportActionBar!!
        actionBar.setDisplayHomeAsUpEnabled(true)

        titleAdapter = TitleAdapter(this)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ItemViewModel::class.java)

        viewModel.state.observe(this, Observer<ItemState> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })

        viewModel.form.addOnPropertyChangedCallback(onPropertyChanged)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        val itemId: Int

        super.onPostCreate(savedInstanceState)
        titleAdapter.onPostCreate(savedInstanceState)

        viewPager.adapter = ItemPagerAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        if (savedInstanceState == null) {
            itemId = intent.getIntExtra(MainApp.PROP_ITEMID, 0)
        } else {
            itemId = savedInstanceState.getInt(MainApp.PROP_ITEMID, 0)
        }

        viewModel.read(itemId)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> =
            fragmentDispatchingAndroidInjector

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        titleAdapter.onSaveInstanceState(outState)

        outState?.putInt(MainApp.PROP_ITEMID, viewModel.form.id)
    }

    override fun onResume() {
        super.onResume()
        viewModel.form.addOnPropertyChangedCallback(onPropertyChanged)
    }

    override fun onPause() {
        viewModel.form.removeOnPropertyChangedCallback(onPropertyChanged)
        super.onPause()
    }

    private fun stateObserver(state: ItemState) {
        if (state.waiting) {
            progressIndicator.show()
            return
        }

        progressIndicator.hide {
            state.item.poll()?.let { item ->
                if (item.id > 0) {
                    titleAdapter.titleId = R.string.it_scoppelletti_cmd_edit
                } else {
                    titleAdapter.titleId = R.string.it_scoppelletti_cmd_new
                }

                viewModel.form.copy(item)
            }

            state.messageId?.poll()?.let { messageId ->
                Snackbar.make(viewPager, messageId, Snackbar.LENGTH_SHORT)
                        .show()
            }

            state.error?.poll()?.let { err ->
                ExceptionDialogFragment.show(this, err)
            }
        }
    }

    override fun onBackPressed() {
        if (onExiting()) {
            super.onBackPressed()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.item, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var menuItem: MenuItem?

        with (viewModel.form) {
            menuItem = menu?.findItem(R.id.cmd_ok)
            menuItem?.isEnabled = id == 0 || changed

            menuItem = menu?.findItem(R.id.cmd_delete)
            menuItem?.isEnabled = id > 0 && !changed
        }

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            android.R.id.home -> {
                onExit()
                return true
            }

            R.id.cmd_ok -> {
                onItemSave()
                return true
            }

            R.id.cmd_delete -> {
                onItemDeleting()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDialogResult(dialogId: Int, which: Int) {
        when (dialogId) {
            TabbedActivity.DLG_DELETE -> when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    onItemDelete()
                }
            }

            TabbedActivity.DLG_EXIT -> when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    if (!isFinishing) {
                        finish()
                    }
                }
            }
        }
    }

    private fun onExit() {
        if (onExiting() && !isFinishing) {
            finish()
        }
    }

    private fun onExiting(): Boolean {
        if (viewModel.form.changed) {
            ConfirmDialogFragment.show(this,
                    R.string.it_scoppelletti_msg_looseChanges,
                    titleId = android.R.string.dialog_alert_title,
                    dialogId = TabbedActivity.DLG_EXIT,
                    affermativeActionTextId = R.string.it_scoppelletti_cmd_exit)
            return false
        }

        return true
    }

    override fun onItemSave() {
        try {
            hideSoftKeyboard()
            if (!viewModel.form.validate()) {
                viewPager.currentItem = 0 // It may depend on which field is
                    // invalid
                return
            }

            if (viewModel.form.id > 0) {
                viewModel.update()
            } else {
                viewModel.create()
            }
        } catch (ex: RuntimeException) {
            progressIndicator.hide {
                ExceptionDialogFragment.show(this, ex)
            }
        }
    }

    private fun onItemDelete() {
        try {
            hideSoftKeyboard()
            viewModel.delete()
        } catch (ex: RuntimeException) {
            progressIndicator.hide {
                ExceptionDialogFragment.show(this, ex)
            }
        }
    }

    private fun onItemDeleting() {
        ConfirmDialogFragment.show(this, R.string.msg_deleting,
                titleId = R.string.it_scoppelletti_cmd_delete,
                dialogId = TabbedActivity.DLG_DELETE,
                affermativeActionTextId = R.string.it_scoppelletti_cmd_delete)
    }

    companion object {
        private const val DLG_DELETE: Int = 1
        private const val DLG_EXIT: Int = 2
    }
}