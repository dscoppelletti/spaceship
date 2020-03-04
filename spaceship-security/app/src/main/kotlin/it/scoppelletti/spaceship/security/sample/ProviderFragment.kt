@file:Suppress("JoinDeclarationAndAssignment")

package it.scoppelletti.spaceship.security.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.security.sample.lifecycle.ProviderViewModel
import kotlinx.android.synthetic.main.provider_fragment.*

class ProviderFragment : Fragment() {

    private lateinit var viewModel: ProviderViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.provider_fragment, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val activity: FragmentActivity

        super.onActivityCreated(savedInstanceState)

        activity = requireActivity()

        activity.hideSoftKeyboard()
        viewModel = ViewModelProvider(this).get(ProviderViewModel::class.java)
        viewModel.state.observe(viewLifecycleOwner, Observer { state ->
            if (state != null) {
                txtContent.text = state
            }
        })

        viewModel.load()
    }

    companion object {
        fun newInstance() = ProviderFragment()
    }
}
