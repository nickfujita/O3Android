package network.o3.o3wallet.Settings

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import network.o3.o3wallet.R


/**
 * Created by drei on 12/8/17.
 */

class SettingsAdapter(context: Context): BaseAdapter() {
    private val mContext: Context
    var settingsTitles = listOf<String>("My Private Key", "Address Book", "Watch-Only-Address",
            "Network", "Theme", "Share", "Contact", "Log out", "Version")
    var images =  listOf(R.drawable.ic_settingsprivatekeyicon, R.drawable.ic_settingsaddressbookicon,
            R.drawable.ic_settingswatchonlyaddressicon, R.drawable.ic_settingsnetworkicon,
            R.drawable.ic_settingsnetworkicon, R.drawable.ic_settingsshareicon,
            R.drawable.ic_settingscontacticon, R.drawable.ic_settingscontacticon,
            R.drawable.ic_settingscontacticon)
    init {
        mContext = context
    }

    enum class CellType {
        PRIVATEKEY, CONTACTS,
        WATCHADRESS, NETWORK,
        THEME, SHARE,
        CONTACT, LOGOUT,
        VERSION

    }

    override fun getItem(position: Int): Pair<String, Int> {
        return Pair(settingsTitles[position], images[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return settingsTitles.count()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.settings_row_layout, viewGroup, false)
        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val subtitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
        titleTextView.text = getItem(position).first
        subtitleTextView.text = ""
        view.findViewById<ImageView>(R.id.settingsIcon).setImageResource(getItem(position).second)

        view.setOnClickListener {
            getClickListenerForPosition(position)
        }
        return view
    }

    fun getClickListenerForPosition(position: Int) {
        if (position == CellType.CONTACTS.ordinal  ) {
            val contactsModal = ContactsFragment.newInstance()
            contactsModal.show((mContext as AppCompatActivity).supportFragmentManager, contactsModal.tag)
            return
        } else if (position == CellType.WATCHADRESS.ordinal) {
            val watchAddressModal = WatchAddressFragment.newInstance()
            watchAddressModal.show((mContext as AppCompatActivity).supportFragmentManager, watchAddressModal.tag)
            return
        }

        Toast.makeText(mContext, position.toString(), Toast.LENGTH_SHORT).show()
    }
}