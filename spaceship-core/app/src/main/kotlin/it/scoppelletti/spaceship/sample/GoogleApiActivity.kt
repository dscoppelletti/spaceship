package it.scoppelletti.spaceship.sample

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import it.scoppelletti.spaceship.app.ExceptionDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.tryFinish
import com.google.android.gms.tasks.Task
import it.scoppelletti.spaceship.app.makeGooglePlayServicesAvailable
import it.scoppelletti.spaceship.applicationException
import it.scoppelletti.spaceship.inject.Injectable
import mu.KotlinLogging
import java.util.concurrent.CancellationException
import javax.inject.Inject

class GoogleApiActivity : AppCompatActivity(),
        Injectable,
        OnDialogResultListener {

    @Inject
    lateinit var exDialogBuilder: ExceptionDialogFragment.Builder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.googleapi_activity)

        makeGooglePlayServicesAvailable()
                .addOnCompleteListener(::onGoogleApiAvailable)
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

        exDialogBuilder.show(this, applicationException {
            message(R.string.it_scoppelletti_err_googleApiNotAvailable)
            cause = ex
        })
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
