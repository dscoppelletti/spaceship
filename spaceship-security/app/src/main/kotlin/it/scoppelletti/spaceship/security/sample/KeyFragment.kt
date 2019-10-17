@file:Suppress("JoinDeclarationAndAssignment")

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
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.app.uiComponent
import it.scoppelletti.spaceship.security.sample.databinding.KeyFragmentBinding
import it.scoppelletti.spaceship.security.sample.lifecycle.KeyForm
import it.scoppelletti.spaceship.security.sample.lifecycle.KeyViewModel
import it.scoppelletti.spaceship.security.sample.lifecycle.MainState
import it.scoppelletti.spaceship.security.sample.lifecycle.MainViewModel

class KeyFragment : Fragment(), DrawerFragment {

    private lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var mainModel: MainViewModel
    private lateinit var keyModel: KeyViewModel
    private lateinit var binding: KeyFragmentBinding

    override val titleId: Int
        get() = R.string.cmd_keygenerator

    init {
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.key_fragment,
                container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtAlias.setOnEditorActionListener { _, _, _ ->
            onGenerate(keyModel.form)
            true
        }

        binding.txtExpire.setOnEditorActionListener { _, _, _ ->
            onGenerate(keyModel.form)
            true
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val activity: FragmentActivity

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()
        viewModelFactory = activity.uiComponent().viewModelFactory()
        mainModel = ViewModelProviders.of(activity, viewModelFactory)
                .get(MainViewModel::class.java)
        keyModel = ViewModelProviders.of(this, viewModelFactory)
                .get(KeyViewModel::class.java)
        binding.model = keyModel.form

        keyModel.state.observe(viewLifecycleOwner,
                Observer<MainState> { state ->
                    if (state != null) {
                        mainModel.setState(state)
                    }
                })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.key, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.cmd_ok -> {
               onGenerate(keyModel.form)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun onGenerate(form: KeyForm) {
        try {
            requireActivity().hideSoftKeyboard()
            if (!form.validate()) {
                return
            }

            keyModel.createSecretKey(form.alias, form.expire)
        } catch (ex: RuntimeException) {
            keyModel.setError(ex)
        }
    }

    companion object {
        fun newInstance() = KeyFragment()
    }
}
