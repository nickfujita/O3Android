package network.o3.o3wallet.Settings

import android.content.ClipData
import android.content.ClipboardManager
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.content.Context
import android.widget.*
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import network.o3.o3wallet.WatchAddress
import network.o3.o3wallet.Wallet.toast


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
            val view = layoutInflater.inflate(R.layout.settings_address_entry_row, viewGroup, false)
            val titleTextView = view.findViewById<TextView>(R.id.addressNickNameTextView)
            val subtitleTextView = view.findViewById<TextView>(R.id.addressTextView)
            titleTextView.text = getItem(position).nickname
            subtitleTextView.text = getItem(position).address
            val optionButton = view.findViewById<ImageButton>(R.id.contact_option_button)

            optionButton.setOnClickListener {
                val popup = PopupMenu(mContext,optionButton)
                popup.menuInflater.inflate(R.menu.contact_menus,popup.menu)
                popup.setOnMenuItemClickListener {
                    val itemId = it.itemId

                    if (itemId == R.id.send_to_address) {
                        mFragment.sendToAddress(getItem(position))
                    } else if (itemId == R.id.remove_address) {
                        mFragment.showRemoveAlert(getItem(position))
                    } else if (itemId == R.id.copy_address) {
                        val clipboard = mContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val contact = getItem(position)
                        val clip = ClipData.newPlainText(mContext.resources.getString(R.string.SETTINGS_address_watch_addresses),contact.address)
                        clipboard.primaryClip = clip
                        mContext.toast(mContext.resources.getString(R.string.WALLET_copied_address))
                    }
                    true
                }
                popup.show()
            }
            return view
        } else {
            val view = layoutInflater.inflate(R.layout.settings_add_address_row, viewGroup, false)
            view.findViewById<Button>(R.id.AddButton).setOnClickListener {
               mFragment.addNewAddress()
            }
            return view
        }
    }
}