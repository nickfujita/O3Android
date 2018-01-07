package network.o3.o3wallet

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.view.ViewPager
import android.widget.Button
import android.widget.Toast
import co.getchannel.channel.Channel
import neowallet.Neowallet
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton

class MainActivity : AppCompatActivity() {
    private lateinit var viewPager: ViewPager
    private lateinit var pagerAdapter: LandingPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        setContentView(R.layout.activity_main)

        val loginButton = findViewById<Button>(R.id.loginButton)
        loginButton.setOnClickListener { loginTapped() }

        val createNewWalletButton = findViewById<Button>(R.id.createNewWallet)
        createNewWalletButton.setOnClickListener { createWalletTapped() }

        val viewPager = findViewById<ViewPager>(R.id.viewPager)
        pagerAdapter = LandingPagerAdapter(supportFragmentManager)
        viewPager.adapter = pagerAdapter

        Channel.setupApplicationContextWithApplicationKey(baseContext,"app_gUHDmimXT8oXRSpJvCxrz5DZvUisko_mliB61uda9iY")
    }

    fun createWalletTapped() {
        if (Account.isEncryptedWalletPresent()) {
            alert ("We've detected that there is already a private key on this device, are you sure you want to replace " +
                    "the current private key with a new one?") {
                yesButton {
                    authenticateReplaceWallet()
                }
                noButton {

                }
            }.show()
        } else {
            Account.createNewWallet()
            val intent = Intent(this@MainActivity, CreateWalletActivity::class.java)
            startActivity(intent)
        }
    }

    fun authenticateReplaceWallet() {
        val mKeyguardManager =  getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!mKeyguardManager.isKeyguardSecure) {
            // Show a message that the user hasn't set up a lock screen.
            Toast.makeText(this,
                    "Secure lock screen hasn't set up.\n"
                            + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
                    Toast.LENGTH_LONG).show()
            return
        } else {
            val intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null)
            if (intent == null) {
                startActivityForResult(intent, 0)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == 0) {
            // Credentials entered successfully!
            if (resultCode == -1) {
                Account.createNewWallet()
                val intent = Intent(this@MainActivity, CreateWalletActivity::class.java)
                startActivity(intent)
            } else {

            }
        }
    }

    fun loginTapped() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }
}
