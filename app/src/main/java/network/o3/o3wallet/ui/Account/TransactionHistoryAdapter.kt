package network.o3.o3wallet.ui.Account

import android.content.Context
import android.graphics.Color
import android.support.v4.content.ContextCompat
import android.view.View
import android.view.LayoutInflater
import android.view.ViewGroup
import network.o3.o3wallet.API.CoZ.TransactionHistoryEntry
import android.widget.*
import network.o3.o3wallet.R

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
        vh.amountTextView.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorSubtitleGrey))
        val t = transactions.get(position)
        if (t.NEO.toInt() == 0) {
            vh.assetTextView.text = "GAS"
            vh.amountTextView.text = "%.8f".format(t.GAS)
            if (t.GAS < 0) {
                vh.amountTextView.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorLoss))
            } else  if (t.GAS > 0) {
                vh.amountTextView.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorGain))
            }
        }
        if (t.GAS == 0.0) {
            vh.assetTextView.text = "NEO"
            vh.amountTextView.text = "%d".format(t.NEO.toInt())
            if (t.NEO.toInt() < 0) {
                vh.amountTextView.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorLoss))
            } else if (t.NEO.toInt() > 0) {
                vh.amountTextView.setTextColor(ContextCompat.getColor(view!!.context, R.color.colorGain))
            }

        }
        vh.transactionIDTextView.text = t.txid

        return view!!
    }

}

private class TransactionHistoryRow(row: View?) {
    public val assetTextView: TextView
    public val transactionIDTextView: TextView
    public val amountTextView: TextView

    init {
        this.assetTextView = row?.findViewById<TextView>(R.id.assetTextView) as TextView
        this.transactionIDTextView = row?.findViewById<TextView>(R.id.transactionIDTextView) as TextView
        this.amountTextView = row?.findViewById<TextView>(R.id.amountTextView) as TextView
    }
}