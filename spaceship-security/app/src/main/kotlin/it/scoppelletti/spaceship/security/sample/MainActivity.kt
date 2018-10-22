package it.scoppelletti.spaceship.security.sample

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.app.showExceptionDialog
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.lifecycle.SingleEvent
import it.scoppelletti.spaceship.security.sample.databinding.MainActivityBinding
import it.scoppelletti.spaceship.security.sample.lifecycle.MainForm
import it.scoppelletti.spaceship.security.sample.lifecycle.MainViewModel
import kotlinx.android.synthetic.main.main_activity.*
import java.lang.RuntimeException
import javax.inject.Inject

class MainActivity : AppCompatActivity(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: MainViewModel
    private lateinit var binding: MainActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.main_activity)
        setSupportActionBar(toolbar)

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(MainViewModel::class.java)
        binding.model = viewModel.form

        viewModel.state.observe(this,
                Observer<SingleEvent<Throwable>> { state ->
            if (state != null) {
                stateObserver(state)
            }
        })
    }

    private fun stateObserver(state: SingleEvent<Throwable>) {
        state.poll()?.let { err ->
            showExceptionDialog(err)
            return
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.cmd_encrypt -> {
                onEncrypt(viewModel.form)
                return true
            }

            R.id.cmd_decrypt -> {
                onDecrypt(viewModel.form)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onEncrypt(form: MainForm) {
        try {
            hideSoftKeyboard()
            if (!form.validate()) {
                return
            }

            viewModel.encrypt(form.alias, form.expire, form.clearText)
        } catch (ex: RuntimeException) {
            showExceptionDialog(ex)
        }
    }

    private fun onDecrypt(form: MainForm) {
        try {
            hideSoftKeyboard()
            if (!form.validate()) {
                return
            }

            viewModel.decrypt(form.alias, form.encryptedText)
        } catch (ex: RuntimeException) {
            showExceptionDialog(ex)
        }
    }
}