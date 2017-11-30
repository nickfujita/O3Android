package network.o3.o3wallet

import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.content.Context
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import network.o3.o3wallet.API.CoZ.TransactionHistoryEntry
import android.widget.*

/**
 * Created by apisit on 11/30/17.
 */
class TransactionHistoryAdapter(context: Context, list: Array<TransactionHistoryEntry>) : BaseAdapter() {

    private val transactions = list
    private val inflator: LayoutInflater

    init {
        this.inflator = LayoutInflater.from(context)
    }

    override fun getCount(): Int {
        return transactions.count()
    }

    override fun getItem(p0: Int): TransactionHistoryEntry {
        return transactions[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val vh: TransactionHistoryRow
        if (convertView == null) {
            view = this.inflator.inflate(R.layout.transaction_history_row_layout, parent, false)
            vh = TransactionHistoryRow(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as TransactionHistoryRow
        }

        vh.assetTextView.text = transactions.get(position).txid

        return view!!
    }

}

private class TransactionHistoryRow(row: View?) {
    public val assetTextView: TextView

    init {
        this.assetTextView = row?.findViewById<TextView>(R.id.assetTextView) as TextView

    }
}