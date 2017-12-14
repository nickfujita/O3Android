package network.o3.o3wallet.Settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import android.support.v7.app.AlertDialog
import android.util.Log
import android.widget.Toast

class AddContact : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_contact)
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

            NeoNodeRPC(PersistentStore.getNodeURL()).validateAddress(addressField.text.toString()) {
                if (it.second != null || it?.first == false) {
                    runOnUiThread {
                        errorAlert.show()
                    }
                } else {
                    PersistentStore.addContact(addressField.text.toString(), nickNameField.text.toString())
                }
            }
        }
    }
}
