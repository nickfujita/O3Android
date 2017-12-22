package network.o3.o3wallet.Wallet

import android.content.Context
import android.support.v4.app.FragmentPagerAdapter
import network.o3.o3wallet.Settings.ContactsFragment

/**
 * Created by apisit on 12/18/17.
 */
class AccountFragmentPagerAdapter(fm: android.support.v4.app.FragmentManager, context: Context) : FragmentPagerAdapter(fm){

    private val PAGE_COUNT = 3
    private val tabTitles = arrayOf("Assets", "Transactions", "Contacts")
    private val context: Context = context


    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): android.support.v4.app.Fragment {
        if (position == 0) {
            return AccountFragment.newInstance()
        }
        if (position == 1) {
            return AccountTransactionsFragment.newInstance()
        }
        if (position == 2) {
            return ContactsFragment.newInstance()
        }
        return ContactsFragment.newInstance()
    }

    override fun getPageTitle(position: Int): CharSequence {
        // Generate title based on item position
        return tabTitles[position]
    }

}