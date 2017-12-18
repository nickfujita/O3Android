package network.o3.o3wallet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.MenuItem
import network.o3.o3wallet.Portfolio.HomeFragment
import network.o3.o3wallet.Settings.SettingsFragment
import network.o3.o3wallet.ui.Account.TabbedAccount


class MainTabbedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_tabbed)
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)
        bottomNavigationView.setOnNavigationItemSelectedListener(object : BottomNavigationView.OnNavigationItemSelectedListener {
            override fun onNavigationItemSelected(item: MenuItem): Boolean {
                var selectedFragment: Fragment? = null
                when (item.getItemId()) {
                    R.id.action_item1 -> selectedFragment = HomeFragment.newInstance()
                    R.id.action_item2 -> selectedFragment = TabbedAccount.newInstance()
                    R.id.action_item3 -> {
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
}
