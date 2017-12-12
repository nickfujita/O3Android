package network.o3.o3wallet.Settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import NeoNodeRPC

class AddWatchAddress : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_watch_address)
        val nickNameField = findViewById<EditText>(R.id.NickNameField)
        val addressField = findViewById<EditText>(R.id.AddressField)
        val button = findViewById<Button>(R.id.AddButton)

        button.setOnClickListener {
            val errorAlert = AlertDialog.Builder(this).create()
            errorAlert.setTitle("Error")
            errorAlert.setMessage("You provided an invalid NEO address, please double check it.")
            errorAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK") {
                _, _ ->
            }

            NeoNodeRPC().validateAddress(addressField.text.toString()) {
                if (it.second != null || it?.first == false) {
                    runOnUiThread {
                        errorAlert.show()
                    }
                } else {
                    PersistentStore.addWatchAddress(addressField.text.toString(), nickNameField.text.toString())
                }
            }
        }

    }
}
