package it.scoppelletti.spaceship.gms.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.Task
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.tryFinish
import it.scoppelletti.spaceship.gms.app.makeGooglePlayServicesAvailable
import it.scoppelletti.spaceship.gms.i18n.GmsMessages
import mu.KotlinLogging
import java.util.concurrent.CancellationException

class MainActivity : AppCompatActivity(), OnDialogResultListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cmd_googleApi -> {
                makeGooglePlayServicesAvailable()
                        .addOnCompleteListener(::onGoogleApiAvailable)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onGoogleApiAvailable(task: Task<Void>) {
        val ex: Throwable?

        if (task.isSuccessful) {
            Toast.makeText(this, R.string.msg_googleApiAvailable,
                    Toast.LENGTH_SHORT).show()
            return
        }

        ex = task.exception
        if (ex is CancellationException) {
            logger.warn("Activity has been completed before the task.")
            return
        }

        showExceptionDialog(ApplicationException(
                GmsMessages.errorGoogleApiNotAvailable(), ex))
    }

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            ExceptionDialogFragment.TAG -> {
                tryFinish()
            }
        }
    }

    private companion object {
        val logger = KotlinLogging.logger {}
    }
}
