package network.o3.o3wallet

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.Button
import neowallet.Neowallet

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: LandingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { loginTapped() }

        val createNewWalletButton = findViewById<Button>(R.id.createNewWallet)
        createNewWalletButton.setOnClickListener { createWalletTapped() }

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        pagerAdapter = LandingPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter
    }

    fun createWalletTapped() {
        Account.createNewWallet()
        val intent = Intent(this, CreateWalletActivity::class.java)
        startActivity(intent)
    }

    fun loginTapped() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
