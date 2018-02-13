package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.AccountAsset
import org.jetbrains.anko.runOnUiThread

/**
 * Created by drei on 12/15/17.
 */

class AssetListAdapter(context: Context, fragment: HomeFragment): BaseAdapter() {
    data class TableCellData(var assetName: String, var assetAmount: Double,
                             var assetPrice: Double, var totalValue: Double, var percentChange: Double)

    private val mContext: Context
    private val mfragment: HomeFragment
    var assets = ArrayList<AccountAsset>()

    init {
        mContext = context
        mfragment = fragment
    }

    override fun getItem(position: Int): TableCellData {
        var assetData = TableCellData("", 0.0, 0.0, 0.0, 0.0)
        assetData.assetName = assets.get(position).name
        assetData.assetAmount = assets.get(position).value
        return assetData
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return assets.count()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.portfolio_asset_card, viewGroup, false)
        val asset = getItem(position)
        val assetNameView = view.findViewById<TextView>(R.id.assetNameTextView)
        val assetPriceView = view.findViewById<TextView>(R.id.assetPriceTextView)
        val assetAmountView = view.findViewById<TextView>(R.id.assetAmountTextView)
        val assetTotalValueView = view.findViewById<TextView>(R.id.totalValueTextView)
        val assetPercentChangeView = view.findViewById<TextView>(R.id.percentChangeTextView)

        assetNameView.text = asset.assetName
       // assetPriceView.text = asset.assetPrice.formattedCurrencyString(homeModel?.getCurrency()!!)
        //assetTotalValueView.text = asset.totalValue.formattedCurrencyString(homeModel?.getCurrency()!!)
        assetPercentChangeView.text = asset.percentChange.formattedPercentString()

        if (asset.percentChange < 0) {
            assetPercentChangeView.setTextColor(ContextCompat.getColor(mContext, R.color.colorLoss))
        } else {
            assetPercentChangeView.setTextColor(ContextCompat.getColor(mContext, R.color.colorGain))
        }

        if (asset.assetName == "NEO") {
            assetAmountView.text = asset.assetAmount.format(0)
        } else {
            assetAmountView.text = asset.assetAmount.format(8)
        }


        view.setOnClickListener {
            val intent = Intent(mfragment.activity, AssetGraph::class.java)
            intent.putExtra("SYMBOL", asset.assetName.capitalize())
            mfragment.activity?.startActivity(intent)
        }

        return view
    }
}