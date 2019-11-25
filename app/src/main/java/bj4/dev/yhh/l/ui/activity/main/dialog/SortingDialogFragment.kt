package bj4.dev.yhh.l.ui.activity.main.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import bj4.dev.yhh.l.R
import bj4.dev.yhh.l.util.SharedPreferenceHelper
import org.koin.android.ext.android.inject

class SortingDialogFragment : DialogFragment() {

    private val sharedPreferenceHelper by inject<SharedPreferenceHelper>()

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.action_sorting)
            .setSingleChoiceItems(
                R.array.sorting_array,
                sharedPreferenceHelper.getDisplayType()
            ) { _, which ->
                sharedPreferenceHelper.setDisplayType(which)
                dismiss()
            }.setNegativeButton(
                android.R.string.cancel
            ) { _, _ -> dismiss() }.create().also {
                it.setCancelable(true)
            }

    }
}