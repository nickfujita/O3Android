package network.o3.o3wallet.Settings

import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.annotation.SuppressLint
import android.widget.*
import network.o3.o3wallet.R


class SettingsFragment : BottomSheetDialogFragment() {
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_settings, null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_settings, container, false)
        val listView = view.findViewById<ListView>(R.id.settingsListView)

        val basicAdapter = SettingsAdapter(this.context)
        listView.adapter = basicAdapter
        return view
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }
}