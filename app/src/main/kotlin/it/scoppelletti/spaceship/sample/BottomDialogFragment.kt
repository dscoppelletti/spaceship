@file:Suppress("JoinDeclarationAndAssignment", "RemoveRedundantQualifierName")

package it.scoppelletti.spaceship.sample

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import it.scoppelletti.spaceship.app.BottomSheetDialogFragmentEx

class BottomDialogFragment : BottomSheetDialogFragmentEx() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_dialog, container, false)
    }

    companion object {

        fun show(activity: FragmentActivity) {
            BottomDialogFragment().show(activity.supportFragmentManager,
                    BottomSheetDialogFragmentEx.TAG)
        }
    }
}
