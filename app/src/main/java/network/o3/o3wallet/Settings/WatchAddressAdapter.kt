package network.o3.o3wallet.Settings

import android.app.Fragment
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.content.Context
import android.content.Intent
import android.widget.*
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import network.o3.o3wallet.WatchAddress


class WatchAddressAdapter(context: Context, fragment: WatchAddressFragment): BaseAdapter() {

    private val mContext: Context
    private val mFragment: WatchAddressFragment
    var watchAddresses = PersistentStore.getWatchAddresses()

    init {
        mContext = context
        mFragment = fragment
    }

    fun updateData() {
        watchAddresses = PersistentStore.getWatchAddresses()
        notifyDataSetChanged()
    }

    override fun getItem(position: Int): WatchAddress {
        return watchAddresses[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return watchAddresses.count() + 1
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)

        if (position != count - 1) {
            val view = layoutInflater.inflate(R.layout.address_entry_row, viewGroup, false)
            val titleTextView = view.findViewById<TextView>(R.id.addressNickNameTextView)
            val subtitleTextView = view.findViewById<TextView>(R.id.addressTextView)
            titleTextView.text = getItem(position).nickname
            subtitleTextView.text = getItem(position).address

            view.setOnClickListener {
                mFragment.showRemoveAlert(getItem(position))
            }

            return view
        } else {
            val view = layoutInflater.inflate(R.layout.add_address_row, viewGroup, false)
            view.findViewById<Button>(R.id.AddButton).setOnClickListener {
                val intent = Intent(mContext, AddWatchAddress::class.java)
                mContext.startActivity(intent)
            }
            return view
        }
    }
}