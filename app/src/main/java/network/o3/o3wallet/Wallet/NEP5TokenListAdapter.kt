package network.o3.o3wallet.Wallet

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import android.widget.TextView
import network.o3.o3wallet.API.NEO.NEP5Token
import network.o3.o3wallet.API.NEO.Node
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.layoutInflater

/**
 * Created by apisit on 12/21/17.
 */

class NEP5TokenListAdapter(context: Context) : BaseAdapter() {

    private val selectedList = PersistentStore.getSelectedNEP5Tokens()
    private var tokens: Array<NEP5Token>? = null
    private val mContext: Context

    init {
        mContext = context
    }

    override fun getCount(): Int {
        return tokens?.count() ?: 0
    }

    override fun getItem(p0: Int): NEP5Token {
        return tokens!![p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    fun setData(data: Array<NEP5Token>) {
        tokens = data
        notifyDataSetChanged()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View?
        val vh: NEP5TokenRow
        val layoutInflater = LayoutInflater.from(mContext)
        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.wallet_nep5_token_row, parent, false)
            vh = NEP5TokenRow(view)
            view.tag = vh
        } else {
            view = convertView
            vh = view.tag as NEP5TokenRow
        }

        vh.tokenNameTextView.text = getItem(position).name
        vh.tokenSymbolTextView.text = getItem(position).symbol

        vh.checkbox.isChecked = selectedList.get(getItem(position).tokenHash) != null
        vh.checkbox.setOnClickListener {
            if (vh.checkbox.isChecked == true) {
                PersistentStore.addToken(getItem(position))
            } else {
                PersistentStore.removeToken(getItem(position))
            }
        }

        return view!!
    }

}

private class NEP5TokenRow(row: View?) {
    public val tokenNameTextView: TextView
    public val tokenSymbolTextView: TextView
    public val checkbox: CheckBox

    init {
        this.tokenNameTextView = row?.findViewById<TextView>(R.id.tokenNameTextView) as TextView
        this.tokenSymbolTextView = row?.findViewById<TextView>(R.id.tokenSymbolTextView) as TextView
        this.checkbox = row?.findViewById<CheckBox>(R.id.nep5TokenCheckbox) as CheckBox
    }
}