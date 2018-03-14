package network.o3.o3wallet

import android.app.Dialog
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import network.o3.o3wallet.Feed.NewsFeedFragment
import network.o3.o3wallet.Portfolio.HomeFragment
import network.o3.o3wallet.Settings.SettingsFragment
import network.o3.o3wallet.Wallet.TabbedAccount
import android.content.DialogInterface
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class MainTabbedActivity : LocalizationActivity() {

    var activeTabID: Int? = 0
    var activeTabPosition: Int? = 0
    var fragments: Array<Fragment>? = arrayOf(HomeFragment.newInstance(),
            TabbedAccount.newInstance(), NewsFeedFragment.newInstance(),
            SettingsFragment.newInstance())

    override fun onBackPressed() {
        alert ("Are you sure you want to exit?", "") {
            yesButton { super.onBackPressed() }
            noButton {  }
        }.show()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tabbar_activity_main_tabbed)


        val selectedFragment = fragments!!.get(0)
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, selectedFragment, 0.toString())
        transaction.commit()

        activeTabID = selectedFragment.id
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var selectedFragment: Fragment? = null
                //avoid loading the data again when tap at the same tab
                if (activeTabID == item.itemId) {
                    return false
                }

                when (item.getItemId()) {
                    R.id.action_item1 -> {
                        switchFragment(0)
                        activeTabID = item.itemId
                        activeTabPosition = 0
                    }
                    R.id.action_item2 -> {
                        switchFragment(1)
                        activeTabID = item.itemId
                        activeTabPosition = 1
                    }
                    R.id.action_item3 -> {
                        switchFragment(2)
                        activeTabID = item.itemId
                        activeTabPosition = 2
                    }
                    R.id.action_item4 -> {
                        val settingsModal = fragments!!.get(3) as SettingsFragment
                        settingsModal.show(supportFragmentManager, settingsModal.tag)
                        return false
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
        if(index == 0) {
            (fragments!!.get(index) as HomeFragment).homeModel.loadAssetsFromModel(false)
        }
    }

}
