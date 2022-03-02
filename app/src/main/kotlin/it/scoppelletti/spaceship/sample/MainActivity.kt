package it.scoppelletti.spaceship.sample

import android.content.DialogInterface
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.AlertDialogFragment
import it.scoppelletti.spaceship.app.showAlertDialog
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.appComponent
import it.scoppelletti.spaceship.i18n.AppMessages
import it.scoppelletti.spaceship.sample.databinding.MainActivityBinding
import mu.KotlinLogging
import java.lang.RuntimeException

private const val PROGRESS_DURATION = 5000L

class MainActivity : AppCompatActivity() {

    private lateinit var appMessages: AppMessages
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        appMessages = appComponent().appMessages()

        supportFragmentManager.setFragmentResultListener(
                AlertDialogFragment.TAG, this) { _, bundle ->
            val which = bundle.getInt(AlertDialogFragment.PROP_RESULT,
                    DialogInterface.BUTTON_NEGATIVE)
            logger.info { "Dialog result: $which." }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cmd_save -> {
                binding.progressIndicator.show()
                binding.contentFrame.postDelayed({
                    binding.progressIndicator.hide()
                }, PROGRESS_DURATION)
            }

            R.id.cmd_alertDialog -> {
                showAlertDialog {
                    message {
                        appMessages.promptSaveChanges()
                    }
                    positiveActionTextId {
                        R.string.it_scoppelletti_cmd_save
                    }
                    negativeActionTextId {
                        android.R.string.cancel
                    }
                }
            }

            R.id.cmd_error -> {
                showExceptionDialog(
                        ApplicationException(appMessages.errorStartActivity(),
                                RuntimeException("Test exception.")))
                return true
            }

            R.id.cmd_bottomDialog -> {
                BottomDialogFragment.show(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
