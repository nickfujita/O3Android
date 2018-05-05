package network.o3.o3wallet.Wallet

import android.content.Context
import android.os.Bundle
import android.support.v4.app.FragmentPagerAdapter
import network.o3.o3wallet.R
import network.o3.o3wallet.Settings.ContactsFragment
import network.o3.o3wallet.Wallet.TransactionHistory.TransactionHistoryFragment

/**
 * Created by apisit on 12/18/17.
 */
class AccountFragmentPagerAdapter(fm: android.support.v4.app.FragmentManager, context: Context) : FragmentPagerAdapter(fm){

    private val PAGE_COUNT = 3
    private val tabTitles = arrayOf(context.resources.getString(R.string.WALLET_assets),
            context.resources.getString(R.string.WALLET_transactions), context.resources.getString(R.string.WALLET_address_book))
    private val context: Context = context


    override fun getCount(): Int {
        return PAGE_COUNT
    }

    override fun getItem(position: Int): android.support.v4.app.Fragment {
        if (position == 0) {
            return AccountFragment.newInstance()
        }
        if (position == 1) {
            return TransactionHistoryFragment.newInstance()
        }
        if (position == 2) {
            val contactsModal = ContactsFragment.newInstance()
            val args = Bundle()
            args.putBoolean("canAddAddress", true)
            contactsModal.arguments = args
            return contactsModal
        }
        val contactsModal = ContactsFragment.newInstance()
        val args = Bundle()
        args.putBoolean("canAddAddress", true)
        contactsModal.arguments = args
        return contactsModal
    }

    override fun getPageTitle(position: Int): CharSequence {
        // Generate title based on item position
        return tabTitles[position]
    }

}