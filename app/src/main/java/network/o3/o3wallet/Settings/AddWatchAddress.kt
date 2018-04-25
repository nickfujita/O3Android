package network.o3.o3wallet.Settings

import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.crashlytics.android.answers.Answers
import com.crashlytics.android.answers.CustomEvent
import com.google.zxing.integration.android.IntentIntegrator
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import network.o3.o3wallet.API.NEO.*
import network.o3.o3wallet.Wallet.Send.afterTextChanged
import org.jetbrains.anko.alert
import org.jetbrains.anko.yesButton

class AddWatchAddress : LocalizationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity_add_watch_address)

        this.title = resources.getString(R.string.watch_address)
        val nickNameField = findViewById<EditText>(R.id.NickNameField)
        val addressField = findViewById<EditText>(R.id.AddressField)
        val saveButton = findViewById<Button>(R.id.AddButton)
        val scanAddressButton = findViewById<Button>(R.id.scanAddressButton)
        scanAddressButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt(resources.getString(R.string.scan_prompt_watch_address))
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }


        val pasteAddressButton = findViewById<Button>(R.id.pasteAddressButton)
        pasteAddressButton.setOnClickListener{
            val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = clipboard.primaryClip
            if (clip != null) {
                val item = clip.getItemAt(0)
                addressField.setText(item.text)
            }
        }
        addressField.afterTextChanged {
            saveButton.isEnabled = addressField.text.trim().toString().count() >0 && nickNameField.text.trim().toString().count() > 0
        }
        nickNameField.afterTextChanged {
            saveButton.isEnabled = addressField.text.trim().toString().count() >0 && nickNameField.text.trim().toString().count() > 0
        }
        saveButton.isEnabled = false
        saveButton.setOnClickListener {
            NeoNodeRPC(PersistentStore.getNodeURL()).validateAddress(addressField.text.trim().toString()) {
                if (it.second != null || it?.first == false) {
                    runOnUiThread {
                        alert (resources.getString(R.string.invalid_neo_address), resources.getString(R.string.error)) {
                            yesButton {  }
                        }.show()
                    }
                } else {
                    PersistentStore.addWatchAddress(addressField.text.trim().toString(), nickNameField.text.trim().toString())
                    Answers().logCustom(CustomEvent("Watch Address Added")
                            .putCustomAttribute("Total Watch Addresses", PersistentStore.getWatchAddresses().count()))
                    finish()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null ) {
            if (result.contents == null) {
                Toast.makeText(this, resources.getString(R.string.cancelled), Toast.LENGTH_LONG).show()
            } else {
                findViewById<EditText>(R.id.AddressField).setText(result.contents)
            }
        }
    }
}
