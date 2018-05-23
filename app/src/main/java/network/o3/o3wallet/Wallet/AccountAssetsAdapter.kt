package network.o3.o3wallet.Wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.API.NEO.AssetType
import network.o3.o3wallet.API.O3Platform.TransferableAsset
import network.o3.o3wallet.API.O3Platform.TransferableAssets
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.runOnUiThread
import java.text.NumberFormat

/**
 * Created by apisit on 12/20/17.
 */
class AccountAssetsAdapter(fragment: AccountFragment, context: Context, address: String, assets: ArrayList<TransferableAsset>) : BaseAdapter() {

    private var arrayOfAccountAssets = assets
    private var address = address
    private var mContext = context
    private val inflator: LayoutInflater
    private val mFragment = fragment


    init {
        this.inflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return arrayOfAccountAssets.count()
    }

    override fun getItem(p0: Int): TransferableAsset {
        return arrayOfAccountAssets[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    fun configureRow(position: Int, vh: AccountAssetRow) {
        val asset = arrayOfAccountAssets[position]
        if (asset.id.contains(NeoNodeRPC.Asset.NEO.assetID())) {
            vh.assetNameTextView.text = NeoNodeRPC.Asset.NEO.name
            vh.assetAmountTextView.text = "%d".format(asset.value.toInt())
        } else if (asset.id.contains(NeoNodeRPC.Asset.GAS.assetID())) {
            vh.assetNameTextView.text = NeoNodeRPC.Asset.GAS.name
            vh.assetAmountTextView.text = "%.8f".format(asset.value)
        } else {
            vh.assetNameTextView.text = asset.symbol
            var formatter = NumberFormat.getNumberInstance()
            formatter.maximumFractionDigits = asset.decimals
            vh.assetAmountTextView.text = formatter.format(asset.value)
        }
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val vh: AccountAssetRow
        if (convertView == null || convertView.tag !is AccountAssetRow) {
            view = this.inflator.inflate(R.layout.wallet_account_asset_row, parent, false)
            vh = AccountAssetRow(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as AccountAssetRow
        }

        configureRow(position,vh)

        val asset = arrayOfAccountAssets[position]
        return view!!

    }

    fun updateAdapter(assets: TransferableAssets) {
        this.arrayOfAccountAssets = assets.assets
        notifyDataSetChanged()
    }
}

class AccountAssetRow(row: View?) {
    val assetNameTextView: TextView
    val assetAmountTextView: TextView

    init {
        this.assetNameTextView = row?.findViewById<TextView>(R.id.assetName) as TextView
        this.assetAmountTextView = row.findViewById<TextView>(R.id.assetAmount) as TextView
    }
}