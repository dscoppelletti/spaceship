package it.scoppelletti.spaceship.sample

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import it.scoppelletti.spaceship.app.hideSoftKeyboard
import it.scoppelletti.spaceship.inject.Injectable
import it.scoppelletti.spaceship.sample.databinding.ItemFragmentBinding
import it.scoppelletti.spaceship.sample.lifecycle.ItemViewModel
import javax.inject.Inject

class ItemTabFragment : Fragment(), Injectable {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private lateinit var viewModel: ItemViewModel
    private lateinit var binding: ItemFragmentBinding

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.item_fragment,
                container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.txtCode.setOnEditorActionListener { _, actionId, _ ->
            onEditorAction(actionId)
        }

        binding.txtDesc.setOnEditorActionListener { _, actionId, _ ->
            onEditorAction(actionId)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        viewModel = ViewModelProviders.of(requireActivity(), viewModelFactory)
                .get(ItemViewModel::class.java)
        binding.model = viewModel.form
    }

    private fun onEditorAction(actionId: Int): Boolean {
        val activity: Activity

        when (actionId) {
            EditorInfo.IME_ACTION_DONE -> {
                activity = requireActivity()
                if (activity is OnItemActionListener &&
                        (viewModel.form.id == 0 || viewModel.form.changed)) {
                    activity.onItemSave()
                } else {
                    activity.hideSoftKeyboard()
                }

                return true
            }
        }

        return false
    }

    companion object {
        fun newInstance() = ItemTabFragment()
    }
}