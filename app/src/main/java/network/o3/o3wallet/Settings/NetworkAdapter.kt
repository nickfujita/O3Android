package network.o3.o3wallet.Settings

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import network.o3.o3wallet.API.NEO.*
import android.graphics.Color
import android.widget.CheckBox
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent

/**
 * Created by drei on 12/11/17.
 */

/**
 * Created by drei on 12/8/17.
 */

class NetworkAdapter(context: Context): BaseAdapter() {

    private val mContext: Context
    private var neoNodes: Array<Node>? = null

    init {
        mContext = context
    }

    fun setData(neoNodes: Array<Node>) {
        this.neoNodes = neoNodes
        notifyDataSetChanged()
    }

    fun getHighestBlockCount(): Int {
        return neoNodes?.maxBy { it.blockcount }?.blockcount!!
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
        val view = layoutInflater.inflate(R.layout.settings_node_entry_row, viewGroup, false)
        val node = getItem(position)
        val urlView = view.findViewById<TextView>(R.id.urlTextView)
        val peerCountView = view.findViewById<TextView>(R.id.peerCountTextView)
        val blockCountView = view.findViewById<TextView>(R.id.blockCountTextView)

        urlView.text = node.url
        peerCountView.text = mContext.resources.getString(R.string.peer_count, node.peercount)
        blockCountView.text = mContext.resources.getString(R.string.block_count, node.blockcount)
        if (getHighestBlockCount() - node.blockcount >= 10) {
            urlView.setTextColor(mContext.getColor(R.color.colorLoss))
            peerCountView.setTextColor(mContext.getColor(R.color.colorLoss))
            blockCountView.setTextColor(mContext.getColor(R.color.colorLoss))
        } else {
            urlView.setTextColor(Color.BLACK)
            peerCountView.setTextColor(mContext.getColor(R.color.colorAccent))
            blockCountView.setTextColor(mContext.getColor(R.color.colorPrimary))
        }

        val checkbox = view.findViewById<CheckBox>(R.id.checkBox)
        if (PersistentStore.getNodeURL() == node.url) {
            checkbox.visibility = View.VISIBLE
            checkbox.isChecked = true
        } else {
            checkbox.visibility = View.INVISIBLE
            checkbox.isChecked = false
        }

        view.setOnClickListener {
            Answers().logCustom(CustomEvent("Network Node Set")
                    .putCustomAttribute("Network Node", node.url))
            PersistentStore.setNodeURL(node.url)
            notifyDataSetChanged()
        }
        return view
    }
}