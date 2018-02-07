package network.o3.o3wallet.Onboarding
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.zxing.integration.android.IntentIntegrator
import network.o3.o3wallet.Account
import network.o3.o3wallet.R
import network.o3.o3wallet.SelectingBestNode
import network.o3.o3wallet.Wallet.toast

class LoginActivity : LocalizationActivity() {
    private lateinit var wifTextfield: TextView
    var isFirstActivityLoad = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity_login)

        val loginButton = findViewById<Button>(R.id.loginButton)
        wifTextfield = findViewById<TextView>(R.id.wipTextView)
        loginButton.setOnClickListener {
            login()
        }

        val scanButton = findViewById<Button>(R.id.loginScanButton)
        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt(resources.getString(R.string.scan_prompt_watch_address))
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }
        //TESTNET Wif testing
        wifTextfield.text = "L4sSGSGh15dtocMMSYS115fhZEVN9UuETWDjgGKu2JDu59yncyVf"
    }

    override fun onResume() {
        super.onResume()
        if (Account.isEncryptedWalletPresent() && isFirstActivityLoad) {
            isFirstActivityLoad = false
            authenticateEncryptedWallet()
        }
    }

    fun authenticateEncryptedWallet() {
        val mKeyguardManager =  getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        if (!mKeyguardManager.isKeyguardSecure) {
            // Show a message that the user hasn't set up a lock screen.
            Toast.makeText(this,
                    resources.getString(R.string.no_passcode_setup),
                    Toast.LENGTH_LONG).show()
                return
        } else {
            val intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null)
            if (intent != null) {
                startActivityForResult(intent, 1)
            }
        }
    }

    /* Can insert these line for testing purposes if necessary
    * Account.fromWIF("L4Ns4Uh4WegsHxgDG49hohAYxuhj41hhxG6owjjTWg95GSrRRbLL") <- This is a testnet WIF
    * PersistentStore.addWatchAddress("ARHusFqxqX4vvkLMzjz2GgPbdeJuXyYrFb", "Test 1")
    * PersistentStore.addWatchAddress("AdrfqqSb9SkBucXu99ZGBtb6YAVu5bJzpu", "Test 2")
    */

    fun login() {
        if (wifTextfield.text.trim().count() == 0) {
            return
        }

        if (wifTextfield.text.trim().count() > 0) {
            val valid = Account.fromWIF(wifTextfield.text.toString())
            if (valid == false) {
                baseContext.toast(resources.getString(R.string.invalid_wif))
                return
            }
        }
        val intent = Intent(this, SelectingBestNode::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents == null) {
            Toast.makeText(this, resources.getString(R.string.cancelled), Toast.LENGTH_LONG).show()
        } else {
            if (requestCode == 1) {
                Account.restoreWalletFromDevice()
                val intent = Intent(this, SelectingBestNode::class.java)
                startActivity(intent)
            } else {
                findViewById<EditText>(R.id.wipTextView).setText(result.contents)
            }
        }
    }
}
