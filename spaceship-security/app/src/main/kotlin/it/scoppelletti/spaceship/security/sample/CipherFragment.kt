package it.scoppelletti.spaceship.security.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.security.sample.databinding.CipherFragmentBinding
import it.scoppelletti.spaceship.security.sample.lifecycle.CipherForm
import it.scoppelletti.spaceship.security.sample.lifecycle.CipherViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.MainState
import it.scoppelletti.spaceship.security.sample.lifecycle.MainViewModel
import javax.inject.Inject

class CipherFragment : Fragment(),
        Injectable,
        DrawerFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var mainModel: MainViewModel
    private lateinit var cipherModel: CipherViewModel
    private lateinit var binding: CipherFragmentBinding

    override val titleId: Int
        get() = R.string.cmd_cipher

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.cipher_fragment,
                container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        mainModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(MainViewModel::class.java)
        cipherModel = ViewModelProviders.of(this, viewModelFactory)
                .get(CipherViewModel::class.java)
        binding.model = cipherModel.form

        cipherModel.state.observe(this, Observer<MainState> { state ->
            if (state != null) {
                mainModel.setState(state)
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        inflater?.inflate(R.menu.cipher, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item?.itemId) {
            R.id.cmd_encrypt -> {
                onEncrypt(cipherModel.form)
                return true
            }

            R.id.cmd_decrypt -> {
                onDecrypt(cipherModel.form)
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onEncrypt(form: CipherForm) {
        try {
            requireActivity().hideSoftKeyboard()
            if (!form.validate()) {
                return
            }

            cipherModel.encrypt(form.alias, form.plainText)
        } catch (ex: RuntimeException) {
            cipherModel.setError(ex)
        }
    }

    private fun onDecrypt(form: CipherForm) {
        try {
            requireActivity().hideSoftKeyboard()
            if (!form.validate()) {
                return
            }

            cipherModel.decrypt(form.alias, form.cipherText)
        } catch (ex: RuntimeException) {
            cipherModel.setError(ex)
        }
    }

    companion object {
        fun newInstance() = CipherFragment()
    }
}
