package it.scoppelletti.spaceship.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import it.scoppelletti.spaceship.ApplicationException
import it.scoppelletti.spaceship.app.AlertDialogFragment
import it.scoppelletti.spaceship.app.DateDialogFragment
import it.scoppelletti.spaceship.app.OnDialogResultListener
import it.scoppelletti.spaceship.app.TimeDialogFragment
import it.scoppelletti.spaceship.app.showAlertDialog
import it.scoppelletti.spaceship.app.showDateDialog
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.app.showTimeDialog
import it.scoppelletti.spaceship.app.appComponent
import it.scoppelletti.spaceship.i18n.AppMessages
import kotlinx.android.synthetic.main.main_activity.*
import mu.KotlinLogging
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalTime
import java.lang.RuntimeException

private const val PROGRESS_DURATION = 5000L

class MainActivity : AppCompatActivity(), OnDialogResultListener,
        DateDialogFragment.OnDateSetListener,
        TimeDialogFragment.OnTimeSetListener {

    private lateinit var appMessages: AppMessages

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_activity)
        setSupportActionBar(toolbar)

        appMessages = appComponent().appMessages()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cmd_save -> {
                progressIndicator.show()
                contentFrame.postDelayed({ progressIndicator.hide() },
                    PROGRESS_DURATION)
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

            R.id.cmd_date -> {
                showDateDialog()
                return true
            }

            R.id.cmd_time -> {
                showTimeDialog()
                return true
            }

            R.id.cmd_bottomDialog -> {
                BottomDialogFragment.show(this)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDialogResult(tag: String, which: Int) {
        when (tag) {
            AlertDialogFragment.TAG -> {
                logger.info { "Dialog result: $which." }
            }
        }
    }

    override fun onDateSet(tag: String, value: LocalDate) {
        logger.info { "Selected date: $value." }
    }

    override fun onTimeSet(tag: String, value: LocalTime) {
        logger.info { "Selected time: $value." }
    }

    private companion object {
        val logger = KotlinLogging.logger { }
    }
}
