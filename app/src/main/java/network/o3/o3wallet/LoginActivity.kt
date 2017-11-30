package network.o3.o3wallet
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val loginButton = findViewById<Button>(R.id.LoginButton)
        loginButton.setOnClickListener {
            login()
        }
    }

    fun login() {
        Account.restoreWalletFromDevice()
        //TODO: REMOVE HARDCODED ADDRESS AND CONTACTS
        //PersistentStore.addWatchAddress("abaceadadfsfadfa", "Hellllllo")
        val intent = Intent(this, MainTabbedActivity::class.java)
        startActivity(intent)
    }
}
