package it.scoppelletti.spaceship.sample

import android.content.Context
import android.os.Bundle
import android.support.annotation.StringRes
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.stub_fragment.*

class StubFragment : Fragment(), DrawerFragment {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.stub_fragment, container, false)

    override val titleId: Int
        get() = arguments?.getInt(StubFragment.PROP_TITLEID) ?: 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        txtTitle.setText(titleId)
    }

    override fun onAttach(context: Context?) {
        super.onAttach(context)
    }

    companion object {
        private const val PROP_TITLEID = "1"

        fun newInstance(@StringRes titleId: Int): StubFragment {
            val args = Bundle().apply {
                putInt(StubFragment.PROP_TITLEID, titleId)
            }

            return StubFragment().apply { arguments = args }
        }
    }
}