package network.o3.o3wallet
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import com.google.gson.Gson
import android.widget.TextView
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_login.*
import com.github.salomonbrys.kotson.*
import com.google.zxing.integration.android.IntentIntegrator
import neowallet.Wallet
import network.o3.o3wallet.API.NEO.ValidatedAddress
import org.jetbrains.anko.doAsync

class LoginActivity : AppCompatActivity() {
    private lateinit var wifTextfield: TextView
    var isFirstActivityLoad = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.LoginButton)
        wifTextfield = findViewById<TextView>(R.id.wipTextView)
        loginButton.setOnClickListener {
            login()
        }

        val scanButton = findViewById<Button>(R.id.ScanButton)
        scanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt("Scan the QR code of the address you want to save")
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }
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
                        "Secure lock screen hasn't set up.\n"
                                + "Go to 'Settings -> Security -> Screenlock' to set up a lock screen",
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
        if (wifTextfield.text.trim().count() > 0) {
            //TODO: VALIDATE ADDRESS
            Account.fromWIF(wifTextfield.text.toString())
        }
        val intent = Intent(this, SelectingBestNode::class.java)
        startActivity(intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents == null) {
            Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
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
