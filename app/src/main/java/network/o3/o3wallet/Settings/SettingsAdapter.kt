package network.o3.o3wallet.Settings

import android.app.KeyguardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri
import network.o3.o3wallet.*
import network.o3.o3wallet.Onboarding.MainActivity
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


/**
 * Created by drei on 12/8/17.
 */

class SettingsAdapter(context: Context, fragment: SettingsFragment): BaseAdapter() {
    private val mContext: Context
    private var mFragment: SettingsFragment
    var settingsTitles = listOf<String>("My Private Key", "Address Book", "Watch-Only-Address",
            "Network", "Contact", "Log out", "Version")
    var images =  listOf(R.drawable.ic_settingsprivatekeyicon, R.drawable.ic_settingsaddressbookicon,
            R.drawable.ic_settingswatchonlyaddressicon, R.drawable.ic_settingsnetworkicon,
            R.drawable.ic_settingscontacticon,
            R.drawable.ic_settingscontacticon, R.drawable.ic_settingscontacticon)
    init {
        mContext = context
        mFragment = fragment
    }

    enum class CellType {
        PRIVATEKEY, CONTACTS,
        WATCHADRESS, NETWORK,
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
        titleTextView.text = getItem(position).first
        if (position == CellType.VERSION.ordinal) {
            titleTextView.text = "Version: " + mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName

        }

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
        } else if (position == CellType.NETWORK.ordinal) {
            val networkModal = NetworkFragment.newInstance()
            networkModal.show((mContext as AppCompatActivity).supportFragmentManager, networkModal.tag)
            return
        } else if (position == CellType.CONTACT.ordinal) {
            val intent = Intent(Intent.ACTION_VIEW)
            val data = Uri.parse("mailto:hello@o3.network")
            intent.data = data
            startActivity(mContext, intent, null)
            return
        } else if (position == CellType.LOGOUT.ordinal) {
            mContext.alert("Are you sure you want to remove your private key from this device?") {
                yesButton {
                    Account.deleteKeyFromDevice()
                    val intent = Intent(mContext, MainActivity::class.java)
                    startActivity(mContext, intent, null)
                }
                noButton {

                }
            }.show()

        } else if (position == CellType.PRIVATEKEY.ordinal) {
            val mKeyguardManager =  mContext.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            if (!mKeyguardManager.isKeyguardSecure) {
                // Show a message that the user hasn't set up a lock screen.
                Toast.makeText(mContext,
                        "Secure lock screen hasn't set up.\n"
                                + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                        Toast.LENGTH_LONG).show();
                return
            } else {
                val intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null)
                if (intent != null) {
                    mFragment.startActivityForResult( intent, 0, null)
                }
            }
        }
    }
}