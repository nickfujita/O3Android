package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.Observer
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.Node
import org.w3c.dom.Text

/**
 * Created by drei on 12/15/17.
 */

class AssetListAdapter(context: Context, fragment: HomeFragment): BaseAdapter() {
    data class TableCellData(var assetName: String, var assetAmount: Double,
                             var assetPrice: Double, var totalValue: Double, var percentChange: Double)

    private val mContext: Context
    private val mfragment: HomeFragment
    var homeModel: HomeViewModel? = null

    init {
        mContext = context
        mfragment = fragment
    }

    override fun getItem(position: Int): TableCellData {
       if (position == 0) {
           var neoData = TableCellData("", 0.0, 0.0, 0.0, 0.0)
           homeModel?.getAccountState(homeModel?.getDisplayType(), false)?.observe(mfragment, Observer<Pair<Int, Double>> { balance ->
               val currentNeoPrice = homeModel!!.getCurrentNeoPrice()
               val firstNeoPrice = homeModel!!.getFirstNeoPrice()

               neoData.assetAmount = balance?.first?.toDouble()!!
               neoData.assetPrice = homeModel?.getCurrentNeoPrice()!!
               neoData.totalValue = balance?.first!! * currentNeoPrice
               neoData.percentChange = (currentNeoPrice - firstNeoPrice) / firstNeoPrice * 100
               notifyDataSetChanged()
           })
           neoData.assetName = "NEO"
           return neoData
       } else {
           var gasData = TableCellData("", 0.0, 0.0, 0.0, 0.0)
           homeModel?.getAccountState(homeModel?.getDisplayType(), false)?.observe(mfragment, Observer<Pair<Int, Double>> { balance ->
               val currentGasPrice = homeModel!!.getCurrentGasPrice()
               val firstGasPrice = homeModel!!.getFirstGasPrice()

               gasData.assetAmount = balance?.second?.toDouble()!!
               gasData.assetPrice = homeModel?.getCurrentGasPrice()!!
               gasData.totalValue = (balance?.second!! * currentGasPrice)
               gasData.percentChange = (currentGasPrice - firstGasPrice) / firstGasPrice * 100

               notifyDataSetChanged()
           })
           gasData.assetName = "GAS"
           return gasData
       }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return 2
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.asset_card, viewGroup, false)
        val asset = getItem(position)
        val assetNameView = view.findViewById<TextView>(R.id.assetNameTextView)
        val assetPriceView = view.findViewById<TextView>(R.id.assetPriceTextView)
        val assetAmountView = view.findViewById<TextView>(R.id.assetAmountTextView)
        val assetTotalValueView = view.findViewById<TextView>(R.id.totalValueTextView)
        val assetPercentChangeView = view.findViewById<TextView>(R.id.percentChangeTextView)

        assetNameView.text = asset.assetName
        assetPriceView.text = asset.assetPrice.formattedCurrencyString(homeModel?.getCurrency()!!)
        assetTotalValueView.text = asset.totalValue.formattedCurrencyString(homeModel?.getCurrency()!!)
        assetPercentChangeView.text = asset.percentChange.formattedPercentString()

        if (asset.assetName == "NEO") {
            assetAmountView.text = asset.assetAmount.format(0)
        } else {
            assetAmountView.text = asset.assetAmount.format(8)
        }


        view.setOnClickListener {
            val intent = Intent(mfragment.activity, AssetGraph::class.java)
            intent.putExtra("SYMBOL", asset.assetName.capitalize())
            mfragment.activity.startActivity(intent)
        }

        return view
    }
}