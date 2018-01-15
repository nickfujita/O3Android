package network.o3.o3wallet.Topup

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.view.PagerAdapter
import network.o3.o3wallet.Onboarding.LandingFeatureScroll

/**
 * Created by drei on 1/12/18.
 */

class TopupTutorialPagerAdapter(fragmentManager: FragmentManager): FragmentStatePagerAdapter(fragmentManager) {
    override fun getItem(position: Int): Fragment {
        return TopupTutorialPage.newInstance(position)
    }

    override fun getCount(): Int {
        return 3
    }
}