package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.Observer
import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import network.o3.o3wallet.API.NEO.Node
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.w3c.dom.Text

/**
 * Created by drei on 12/15/17.
 */

class AssetListAdapter(context: Context, fragment: HomeFragment): BaseAdapter() {
    data class TableCellData(var assetName: String, var assetAmount: Double, var assetPrice: Double, var totalValue: Double)

    private val mContext: Context
    private val mfragment: HomeFragment
    var homeModel: HomeViewModel? = null

    init {
        mContext = context
        mfragment = fragment
    }

    override fun getItem(position: Int): TableCellData {
       // if (position == 0) {
            var neoData = TableCellData("", 0.0, 0.0,0.0)
            homeModel?.getAccountState(homeModel?.getDisplayType(), false)?.observe(mfragment,  Observer<Pair<Int, Double>> { balance ->
                neoData.assetAmount = balance?.first?.toDouble()!!
                neoData.assetPrice = homeModel?.getCurrentNeoPrice()!!
                notifyDataSetChanged()
            })
            neoData.assetName = "NEO"
            return neoData

        //}
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

        assetNameView.text = asset.assetName
        assetPriceView.text = asset.assetPrice.toString()
        assetAmountView.text = asset.assetAmount.toString()

        return view
    }
}