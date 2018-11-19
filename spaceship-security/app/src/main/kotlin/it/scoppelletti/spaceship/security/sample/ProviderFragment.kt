package it.scoppelletti.spaceship.security.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.security.sample.lifecycle.ProviderViewModel
import kotlinx.android.synthetic.main.provider_fragment.*
import javax.inject.Inject

class ProviderFragment : Fragment(),
        Injectable,
        DrawerFragment {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ProviderViewModel

    override val titleId: Int
        get() = R.string.cmd_providers

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.provider_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        requireActivity().hideSoftKeyboard()

        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ProviderViewModel::class.java)
        viewModel.state.observe(this, Observer<CharSequence> { state ->
            if (state != null) {
                txtContent.text = state
            }
        })

        viewModel.load()
    }

    companion object {
        fun newInstance(): ProviderFragment = ProviderFragment()
    }
}