package network.o3.o3wallet

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.design.widget.Snackbar
import android.support.v7.app.ActionBar
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toolbar
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.ui.toast
import network.o3.o3wallet.ui.toastUntilCancel

class SendActivity : AppCompatActivity() {

    lateinit var addressTextView: TextView
    lateinit var amountTextView: TextView
    lateinit var selectedAsset: NeoNodeRPC.Asset
    lateinit var noteTextView: TextView
    lateinit var selectedAssetTextView: TextView
    lateinit var sendButton: Button
    lateinit var pasteAddressButton: Button
    lateinit var scanAddressButton: Button
    lateinit var selectAddressButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send)

        this.title = "Send"
        //default asset
        selectedAsset = NeoNodeRPC.Asset.NEO

        addressTextView = findViewById<TextView>(R.id.addressTextView)
        amountTextView = findViewById<TextView>(R.id.amountTextView)
        noteTextView = findViewById<TextView>(R.id.noteTextView)
        sendButton = findViewById<Button>(R.id.sendButton)
        pasteAddressButton = findViewById<Button>(R.id.pasteAddressButton)
        scanAddressButton = findViewById<Button>(R.id.scanAddressButton)
        selectAddressButton = findViewById<Button>(R.id.selectAddressButton)
        selectedAssetTextView = findViewById<TextView>(R.id.selectedAssetTextView)

        selectedAssetTextView.text = selectedAsset.name.toUpperCase()
        addressTextView.afterTextChanged { checkEnableSendButton() }
        amountTextView.afterTextChanged { checkEnableSendButton() }
        selectedAssetTextView.setOnClickListener { toggleAsset() }

        sendButton.isEnabled = false
        sendButton.setOnClickListener { sendTapped() }

        pasteAddressButton.setOnClickListener { pasteAddressTapped() }
        scanAddressButton.setOnClickListener { scanAddressTapped() }
        selectAddressButton.setOnClickListener { selectAddressTapped() }
    }

    fun checkEnableSendButton() {
        sendButton.isEnabled = (addressTextView.text.trim().count() > 0 && amountTextView.text.count() > 0)
    }

    fun toggleAsset() {
        if (selectedAsset == NeoNodeRPC.Asset.NEO) {
            selectedAsset = NeoNodeRPC.Asset.GAS
            amountTextView.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
        } else {
            selectedAsset = NeoNodeRPC.Asset.NEO
            amountTextView.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED)
            var amount = amountTextView.text.trim().toString().toDouble()
            if (amount < 1) {
                amountTextView.text = "1"
            }
        }
        selectedAssetTextView.text = selectedAsset.name.toUpperCase()
    }

    fun sendTapped() {
        //validate field
        val address = addressTextView.text.trim().toString()
        var amount = amountTextView.text.trim().toString().toDouble()

        if (amount == 0.0) {
            baseContext.toast("Amount cannot be zero")
            return
        }
        val wallet = Account.getWallet()
        val toast = baseContext.toastUntilCancel("Sending...")
        sendButton.isEnabled = false
        NeoNodeRPC().sendAssetTransaction(wallet!!, this.selectedAsset, amount, address, null) {
            runOnUiThread {
                toast.cancel()
                val error = it.second
                val success = it.first
                if (success == true) {
                    baseContext!!.toast("Sent Successfully")
                    Handler().postDelayed(Runnable {
                        finish()
                    },1000)
                } else {
                    baseContext!!.toast("Error while sending. Please check your network")
                }
            }
        }
    }

    fun pasteAddressTapped() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = clipboard.primaryClip
        if (clip != null) {
            val item = clip.getItemAt(0)
            addressTextView.text = item.text.toString()
        }
    }


    fun scanAddressTapped() {

    }

    fun selectAddressTapped() {

    }

}

fun TextView.afterTextChanged(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun afterTextChanged(editable: Editable?) {
            afterTextChanged.invoke(editable.toString())
        }
    })
}