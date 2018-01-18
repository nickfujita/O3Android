package network.o3.o3wallet.Wallet.Send

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListAdapter
import android.widget.TextView
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.API.NEO.NEP5Token
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.layoutInflater

/**
 * Created by drei on 1/18/18.
 */

class AssetSelectorAdapter(context: Context, fragment: AssetSelectorDialog, assets: ArrayList<AccountAsset>): BaseAdapter() {
    private val mContext: Context
    private val mFragment: AssetSelectorDialog
    private val mAssets: ArrayList<AccountAsset>
    private val inflator: LayoutInflater

    init {
        mContext = context
        mFragment = fragment
        mAssets = assets
        inflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return mAssets.count()  + 2
    }

    override fun getItem(p0: Int): AccountAsset? {
        return when(p0) {
            0 ->  null
            1,2 -> mAssets[p0 - 1]
            3 -> null
            else -> mAssets[p0 - 4]
        }
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val item = getItem(position)
        when (position) {
            0 -> {
                val view = inflator.inflate(R.layout.wallet_fragment_asset_row_header, parent, false)
                view.findViewById<TextView>(R.id.headerTextView).text = mContext.resources.getString(R.string.native_assets_header)
                return view
            } 3 -> {
                val view = inflator.inflate(R.layout.wallet_fragment_asset_row_header, parent, false)
                view.findViewById<TextView>(R.id.headerTextView).text = mContext.resources.getString(R.string.token_assets_header)
                return view
            } 1, 2 -> {
                val view = inflator.inflate(R.layout.wallet_fragment_asset_row, parent, false)
                view.findViewById<TextView>(R.id.assetShortNameTextView).text = item!!.name
                view.findViewById<TextView>(R.id.assetAmountTextView).text = item!!.value.toString()
                view.findViewById<TextView>(R.id.assetLongNameTextView).visibility = View.INVISIBLE
                return view
            } else -> {
                val view = inflator.inflate(R.layout.wallet_fragment_asset_row, parent, false)
                view.findViewById<TextView>(R.id.assetShortNameTextView).text = item!!.name
                view.findViewById<TextView>(R.id.assetAmountTextView).text = item!!.value.toString()
                view.findViewById<TextView>(R.id.assetLongNameTextView).visibility = View.VISIBLE
                return view
            }
        }
    }
}