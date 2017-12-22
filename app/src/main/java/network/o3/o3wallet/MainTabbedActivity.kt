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
import network.o3.o3wallet.ui.Account.AccountFragment
import network.o3.o3wallet.ui.Account.TabbedAccount
import java.lang.ref.WeakReference


class MainTabbedActivity : AppCompatActivity() {

    var activeTabID: Int? = 0
    var activeTabPosition: Int? = 0
    var fragments: Array<Fragment>? = arrayOf(HomeFragment.newInstance(),
            TabbedAccount.newInstance(), NewsFeedFragment.newInstance(),
            SettingsFragment.newInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabbed)


        val selectedFragment = fragments!!.get(0)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment, 0.toString())
        transaction.commit()
        setupChannel()

        activeTabID = selectedFragment.id
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var selectedFragment: Fragment? = null
                //avoid loading the data again when tap at the same tab
                if (activeTabID == item.itemId) {
                    return false
                }
                activeTabID = item.itemId
                when (item.getItemId()) {
                    R.id.action_item1 -> {
                        switchFragment(0)
                        activeTabPosition = 0
                    }
                    R.id.action_item2 -> {
                        switchFragment(1)
                        activeTabPosition = 1
                    }
                    R.id.action_item3 -> {
                        switchFragment(2)
                        activeTabPosition = 2
                    }
                    R.id.action_item4 -> {
                        val settingsModal = fragments!!.get(3) as SettingsFragment
                        settingsModal.show(supportFragmentManager, settingsModal.tag)
                        return true
                    }
                }
                return true
            }
        })
    }

    private fun switchFragment(index: Int) {

        val transaction = supportFragmentManager.beginTransaction()

        // if the fragment has not yet been added to the container, add it first
        if (supportFragmentManager.findFragmentByTag(index.toString()) == null) {
            transaction.add(R.id.frame_layout, fragments!!.get(index), index.toString())
        }

        transaction.hide(fragments!!.get(activeTabPosition!!))
        transaction.show(fragments!!.get(index))
        transaction.commit()
    }

    fun setupChannel() {
        Channel.setupActivityWithApplicationKey(WeakReference(this),"app_gUHDmimXT8oXRSpJvCxrz5DZvUisko_mliB61uda9iY",Account.getWallet()!!.address.toString(),null)
        // Get updated InstanceID token.
        val refreshedToken = FirebaseInstanceId.getInstance().token
        Channel.saveDeviceToken(refreshedToken)
    }
}
