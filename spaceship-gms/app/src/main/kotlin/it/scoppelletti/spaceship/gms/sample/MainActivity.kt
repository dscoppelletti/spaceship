@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.gms.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.gms.app.gmsComponent
import it.scoppelletti.spaceship.gms.i18n.GmsMessages

class MainActivity : AppCompatActivity(), OnDialogResultListener {

    private lateinit var viewModel: MainViewModel
    private lateinit var gmsMessages: GmsMessages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        gmsMessages = gmsComponent().gmsMessages()

        viewModel.state.observe(this, Observer { state ->
            state?.poll()?.let {
                stateObserver(it)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cmd_googleApi -> {
                viewModel.makeGmsAvailable(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun stateObserver(state: GoogleApiState) {
        if (state.err == null) {
            Toast.makeText(this, R.string.msg_googleApiAvailable,
                    Toast.LENGTH_SHORT).show()
        } else {
            showExceptionDialog(ApplicationException(
                    gmsMessages.errorGoogleApiNotAvailable(), state.err))
        }
    }

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            ExceptionDialogFragment.TAG -> {
                tryFinish()
            }
        }
    }
}
