package network.o3.o3wallet.Settings

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Button
import android.widget.TextView
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import network.o3.o3wallet.Contact
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import NeoNetwork
import Node

/**
 * Created by drei on 12/11/17.
 */

/**
 * Created by drei on 12/8/17.
 */

class NetworkAdapter(context: Context): BaseAdapter() {

    private val mContext: Context
    private var neoNodes: Array<Node>? = nil

    init {
        mContext = context
    }

    fun setData(neoNodes: Array<Node>) {
        this.neoNodes = neoNodes
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): Node {
        return neoNodes!![position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return neoNodes?.count() ?: 0
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)

        /*if (position != getCount() - 1) {
            val view = layoutInflater.inflate(R.layout.address_entry_row, viewGroup, false)
            val titleTextView = view.findViewById<TextView>(R.id.addressNickNameTextView)
            val subtitleTextView = view.findViewById<TextView>(R.id.addressTextView)
            titleTextView.text = getItem(position).nickname
            subtitleTextView.text = getItem(position).address
            return view
        } else {
            val view = layoutInflater.inflate(R.layout.add_address_row, viewGroup, false)
            view.findViewById<Button>(R.id.AddButton).setOnClickListener {
                val intent = Intent(mContext, AddContact::class.java)
                mContext.startActivity(intent)
            }
            return view
        }*/
    }
}