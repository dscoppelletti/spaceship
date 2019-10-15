
@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.sample

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.Observable
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.snackbar.Snackbar
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.TitleAdapter
import it.scoppelletti.spaceship.app.uiComponent
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.app.showAlertDialog
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.i18n.UIMessages
import it.scoppelletti.spaceship.sample.i18n.SampleMessages
import it.scoppelletti.spaceship.sample.lifecycle.ItemState
import it.scoppelletti.spaceship.sample.lifecycle.ItemViewModel
import it.scoppelletti.spaceship.sample.widget.ItemPagerAdapter
import kotlinx.android.synthetic.main.tabbed_activity.*

class TabbedActivity : AppCompatActivity(), OnDialogResultListener,
        OnItemActionListener {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
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

        viewModelFactory = uiComponent().viewModelFactory()
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

        viewPager.adapter = ItemPagerAdapter(this, supportFragmentManager)
        tabLayout.setupWithViewPager(viewPager)

        itemId = savedInstanceState?.getInt(MainApp.PROP_ITEMID, 0) ?:
            intent.getIntExtra(MainApp.PROP_ITEMID, 0)

        setState(itemId)
        viewModel.read(itemId)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(MainApp.PROP_ITEMID, viewModel.form.id)
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

        progressIndicator.hide()
        state.item.poll()?.let { item ->
            setState(item.id)
            viewModel.form.copy(item)
        }

        state.messageId?.poll()?.let { messageId ->
            Snackbar.make(viewPager, messageId, Snackbar.LENGTH_SHORT)
                    .show()
        }

        state.error?.poll()?.let { err ->
            showExceptionDialog(err)
        }
    }

    private fun setState(itemId: Int) {
        if (itemId > 0) {
            titleAdapter.titleId = R.string.it_scoppelletti_cmd_edit
        } else {
            titleAdapter.titleId = R.string.it_scoppelletti_cmd_new
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

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            MainApp.TAG_SAVEDLG -> when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    onItemSave()
                }

                DialogInterface.BUTTON_NEUTRAL -> {
                    tryFinish()
                }
            }

            MainApp.TAG_DELETEDLG -> when (which) {
                DialogInterface.BUTTON_POSITIVE -> {
                    onItemDelete()
                }
            }
        }
    }

    private fun onExit() {
        if (onExiting()) {
            tryFinish()
        }
    }

    private fun onExiting(): Boolean {
        if (viewModel.form.changed) {
            showAlertDialog {
                tag {
                    MainApp.TAG_SAVEDLG
                }
                message {
                    UIMessages.promptSaveChanges()
                }
                icon {
                    android.R.drawable.ic_dialog_alert
                }
                positiveActionText {
                    R.string.it_scoppelletti_cmd_save
                }
                neutralActionText {
                    R.string.it_scoppelletti_cmd_dontSave
                }
            }

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
            progressIndicator.hide()
            showExceptionDialog(ex)
        }
    }

    private fun onItemDelete() {
        try {
            hideSoftKeyboard()
            viewModel.delete()
        } catch (ex: RuntimeException) {
            progressIndicator.hide()
            showExceptionDialog(ex)
        }
    }

    private fun onItemDeleting() {
        showAlertDialog {
            tag {
                MainApp.TAG_DELETEDLG
            }
            message {
                SampleMessages.promptDeleting()
            }
            title {
                R.string.it_scoppelletti_cmd_delete
            }
            positiveActionText {
                R.string.it_scoppelletti_cmd_delete
            }
        }
    }
}