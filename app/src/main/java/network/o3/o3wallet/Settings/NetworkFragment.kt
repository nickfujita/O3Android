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
import Node
import network.o3.o3wallet.API.O3.Portfolio
import network.o3.o3wallet.R

/**
 * Created by drei on 12/11/17.
 */

class NetworkFragment: BottomSheetDialogFragment() {
    var networkModel: NetworkViewModel? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_network, null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        networkModel = NetworkViewModel()
        val view = inflater!!.inflate(R.layout.network, container, false)


        val listView = view.findViewById<ListView>(R.id.nodesListView)

        val networkAdapter = NetworkAdapter(this.context)
        listView.adapter = networkAdapter

        networkModel?.getNodesFromModel(refresh = true)?.observe(this,  Observer<Array<Node>> { nodes ->
            networkAdapter.setData(nodes!!)
        })
        return view
    }

    companion object {
        fun newInstance(): ContactsFragment {
            return ContactsFragment()
        }
    }
}