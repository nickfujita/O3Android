package network.o3.o3wallet.Settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import network.o3.o3wallet.API.NEO.*
import android.widget.TextView
import network.o3.o3wallet.R

/**
 * Created by drei on 12/11/17.
 */

class NetworkFragment: BottomSheetDialogFragment() {
    var networkModel: NetworkViewModel? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.settings_fragment_network, null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        networkModel = NetworkViewModel()
        val view = inflater!!.inflate(R.layout.settings_fragment_network, container, false)
        val headerView = layoutInflater.inflate(R.layout.settings_header_row, null)
        headerView.findViewById<TextView>(R.id.headerTextView).text = resources.getString(R.string.SETTINGS_available_nodes)

        val listView = view.findViewById<ListView>(R.id.nodeListView)
        listView.addHeaderView(headerView)

        val networkAdapter = NetworkAdapter(this.context!!)
        listView.adapter = networkAdapter

        networkModel?.getNodesFromModel(refresh = true)?.observe(this,  Observer<Array<Node>> { nodes ->
            networkAdapter.setData(nodes!!)
        })
        return view
    }

    companion object {
        fun newInstance(): NetworkFragment {
            return NetworkFragment()
        }
    }
}