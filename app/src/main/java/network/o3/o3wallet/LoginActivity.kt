package network.o3.o3wallet
import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import com.google.gson.Gson
import android.widget.TextView
import kotlinx.android.synthetic.main.activity_login.*
import com.github.salomonbrys.kotson.*
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

        //Account.fromWIF(wifTextfield.text.toString())

        Account.restoreWalletFromDevice()
        //TODO: REMOVE HARDCODED ADDRESS AND CONTACTS
      //  PersistentStore.removeWatchAddress("abaceadadfsfadfa", "Hellllllo")
      //  PersistentStore.addWatchAddress("AJy6mZwSH8pWC4eHBAAcbkztcufTw51rfE", "Test 1")
       // PersistentStore.addWatchAddress("AdrfqqSb9SkBucXu99ZGBtb6YAVu5bJzpu", "Test 2")
        //PersistentStore.addContact("AdrfqqSb9SkBucXu99ZGBtb6YAVu5bJzpu", "Bittrex Address")
        val intent = Intent(this, MainTabbedActivity::class.java)
        startActivity(intent)
    }
}
