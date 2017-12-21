package network.o3.o3wallet.ui.Account

import android.os.Bundle
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.support.design.widget.TabLayout
import network.o3.o3wallet.R
import android.support.v4.app.Fragment

class TabbedAccount : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_tabbed_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        val tabLayout = view.findViewById<TabLayout>(R.id.tabLayout)

        viewPager.adapter = AccountFragmentPagerAdapter(childFragmentManager, context!!)
        tabLayout.setupWithViewPager(viewPager)
    }

    companion object {
        fun newInstance(): TabbedAccount {
            return TabbedAccount()
        }
    }
}
