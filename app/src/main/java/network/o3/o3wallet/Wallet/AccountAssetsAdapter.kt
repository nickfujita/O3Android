package network.o3.o3wallet.Wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.R
import java.text.NumberFormat

/**
 * Created by apisit on 12/20/17.
 */
class AccountAssetsAdapter(context: Context, assets: Array<AccountAsset>) : BaseAdapter() {

    private var assets = assets
    private val inflator: LayoutInflater

    init {
        this.inflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return assets.count()
    }

    override fun getItem(p0: Int): AccountAsset {
        return assets[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val vh: AccountAssetRow
        if (convertView == null) {
            view = this.inflator.inflate(R.layout.account_asset_row, parent, false)
            vh = AccountAssetRow(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as AccountAssetRow
        }

        val asset = assets[position]
        if (asset.assetID.contains(NeoNodeRPC.Asset.NEO.assetID())) {
            vh.assetNameTextView.text = NeoNodeRPC.Asset.NEO.name
            vh.assetAmountTextView.text = "%d".format(asset.value.toInt())
        } else if (asset.assetID.contains(NeoNodeRPC.Asset.GAS.assetID())) {
            vh.assetNameTextView.text = NeoNodeRPC.Asset.GAS.name
            vh.assetAmountTextView.text = "%.8f".format(asset.value)
        } else {
            vh.assetNameTextView.text = asset.symbol
            var formatter = NumberFormat.getNumberInstance()
            formatter.maximumFractionDigits = asset.decimal
            vh.assetAmountTextView.text = formatter.format(asset.value)
        }

        return view!!
    }

    public fun updateAdapter(assets: Array<AccountAsset>) {
        this.assets = assets
        notifyDataSetChanged()
    }
}
private class AccountAssetRow(row: View?) {
    val assetNameTextView: TextView
    val assetAmountTextView: TextView

    init {
        this.assetNameTextView = row?.findViewById<TextView>(R.id.assetName) as TextView
        this.assetAmountTextView = row?.findViewById<TextView>(R.id.assetAmount) as TextView
    }
}