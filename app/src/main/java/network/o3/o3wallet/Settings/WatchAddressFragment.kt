/**
 * Created by drei on 12/11/17.
 */
package network.o3.o3wallet.Settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import network.o3.o3wallet.R


/**
 * Created by drei on 12/8/17.
 */

class WatchAddressFragment : BottomSheetDialogFragment() {
    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_watch_address, null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_watch_address, container, false)
        val headerView = layoutInflater.inflate(R.layout.settings_header, null)
        headerView.findViewById<TextView>(R.id.headerTextView).text = "Watch Addresses"

        val listView = view.findViewById<ListView>(R.id.watchAddressListView)
        listView.addHeaderView(headerView)

        val basicAdapter = WatchAddressAdapter(this.context)
        listView.adapter = basicAdapter
        return view
    }

    companion object {
        fun newInstance(): WatchAddressFragment {
            return WatchAddressFragment()
        }
    }
}