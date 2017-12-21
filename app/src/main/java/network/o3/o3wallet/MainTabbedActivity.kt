package network.o3.o3wallet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import co.getchannel.channel.Channel
import com.google.firebase.iid.FirebaseInstanceId
import network.o3.o3wallet.Feed.NewsFeedFragment
import network.o3.o3wallet.Portfolio.HomeFragment
import network.o3.o3wallet.Settings.SettingsFragment
import network.o3.o3wallet.ui.Account.TabbedAccount
import java.lang.ref.WeakReference


class MainTabbedActivity : AppCompatActivity() {
    var savedNewsFragment: NewsFeedFragment? = null

    var activeTab: Int? = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabbed)


        val selectedFragment = HomeFragment.newInstance()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment)
        transaction.commit()
        setupChannel()

        activeTab = selectedFragment.id
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var selectedFragment: Fragment? = null
                //avoid loading the data again when tap at the same tab
                if (activeTab == item.itemId) {
                    return false
                }
                activeTab = item.itemId
                when (item.getItemId()) {
                    R.id.action_item1 -> {
                        selectedFragment = HomeFragment.newInstance()
                    }
                    R.id.action_item2 -> {
                        selectedFragment = TabbedAccount.newInstance()
                    }
                    R.id.action_item3 -> {
                        if (savedNewsFragment == null) {
                            selectedFragment = NewsFeedFragment.newInstance()
                            savedNewsFragment = selectedFragment
                        }  else {
                            selectedFragment = savedNewsFragment
                        }

                    }
                    R.id.action_item4 -> {
                        val settingsModal = SettingsFragment.newInstance()
                        settingsModal.show(supportFragmentManager, settingsModal.tag)
                        return true
                    }
                }
                val transaction = supportFragmentManager.beginTransaction()
                transaction.replace(R.id.frame_layout, selectedFragment)
                transaction.commit()
                return true
            }
        })
    }

    fun setupChannel() {
        Channel.setupActivityWithApplicationKey(WeakReference(this),"app_gUHDmimXT8oXRSpJvCxrz5DZvUisko_mliB61uda9iY",Account.getWallet()!!.address.toString(),null)
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Channel.saveDeviceToken(refreshedToken)
    }
}
