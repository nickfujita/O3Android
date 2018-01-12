package network.o3.o3wallet.Topup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.topup_activity_topup_tutorial.*
import network.o3.o3wallet.R
import android.support.design.widget.TabLayout



class TopupTutorial : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_topup_tutorial)
        tutorialPager.adapter = TopupTutorialPagerAdapter(supportFragmentManager)
        tabLayout.setupWithViewPager(tutorialPager, true)
    }
}
