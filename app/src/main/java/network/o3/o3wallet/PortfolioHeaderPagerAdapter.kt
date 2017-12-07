package network.o3.o3wallet

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.app.FragmentPagerAdapter

import android.util.Log

/**
 * Created by drei on 11/26/17.
 */
class PortfolioHeaderPagerAdapter(fragmentManager: FragmentManager): FragmentPagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return PortfolioHeader.newInstance(position)
    }

    override fun getCount(): Int {
        return 3
    }
}