package network.o3.o3wallet.Settings

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v7.app.AlertDialog
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import network.o3.o3wallet.API.NEO.*

class AddWatchAddress : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_watch_address)
        val nickNameField = findViewById<EditText>(R.id.NickNameField)
        val addressField = findViewById<EditText>(R.id.AddressField)
        val button = findViewById<Button>(R.id.AddButton)

        val actionButton = findViewById<FloatingActionButton>(R.id.floatingActionButton)

        actionButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt("Scan the QR code of the address you want to save")
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }

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
                    PersistentStore.addWatchAddress(addressField.text.toString(), nickNameField.text.toString())
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null ) {
            if (result.contents == null) {
                Toast.makeText(this, "Cancelled", Toast.LENGTH_LONG).show()
            } else {
                findViewById<EditText>(R.id.AddressField).setText(result.contents)
            }
        }
    }
}
