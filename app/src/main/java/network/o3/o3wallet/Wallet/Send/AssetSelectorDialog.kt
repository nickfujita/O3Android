package network.o3.o3wallet.Wallet.Send

import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.Portfolio.AssetListAdapter
import network.o3.o3wallet.R
import network.o3.o3wallet.Wallet.NEP5TokenListAdapter
import org.jetbrains.anko.support.v4.onUiThread
import org.jetbrains.anko.support.v4.uiThread

/**
 * Created by drei on 1/18/18.
 */

class AssetSelectorDialog: BottomSheetDialogFragment() {
    var assets: ArrayList<AccountAsset> = arrayListOf()

    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.wallet_fragment_send_asset_list,null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.wallet_fragment_send_asset_list, container, false)
        val listView = view.findViewById<ListView>(R.id.assetListView)
        val adapter = AssetSelectorAdapter(this.context, this, assets)
        listView.adapter = adapter
        return view
    }
}