package network.o3.o3wallet
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import neowallet.Wallet

class LoginActivity : AppCompatActivity() {
    private lateinit var wifTextfield: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.LoginButton)
        wifTextfield = findViewById<TextView>(R.id.wipTextView)
        loginButton.setOnClickListener {
            login()
        }
    }

    fun login() {

        Account.fromWIF(wifTextfield.text.toString())
        //Account.restoreWalletFromDevice()
        //TODO: REMOVE HARDCODED ADDRESS AND CONTACTS
        //PersistentStore.addWatchAddress("abaceadadfsfadfa", "Hellllllo")
        val intent = Intent(this, MainTabbedActivity::class.java)
        startActivity(intent)
    }
}
