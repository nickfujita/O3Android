package network.o3.o3wallet.Topup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.support.design.widget.Snackbar
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.util.Log
import android.widget.Button
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.topup_activity_send_amount.*
import kotlinx.android.synthetic.main.wallet_activity_send.*
import neowallet.Neowallet
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.Wallet.toast
import network.o3.o3wallet.Wallet.toastUntilCancel
import org.jetbrains.anko.backgroundColor


class TopupSendAmountActivity : AppCompatActivity() {
    var coldStorageWIF: String? = null
    private var selectedAsset = NeoNodeRPC.Asset.NEO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_send_amount)
        val scanButton = findViewById<Button>(R.id.scanButton)
        assetTextView.setOnClickListener { toggleAsset() }
        scanButton.setOnClickListener {scanButtonTapped()}
    }

    fun scanButtonTapped() {
        val integrator = IntentIntegrator(this)
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
        integrator.setPrompt(resources.getString(R.string.scan_prompt_watch_address))
        integrator.setOrientationLocked(false)
        integrator.initiateScan()
    }

    private fun finishTransaction() {
        //validate field
        var amount = topupAmountTextView.text.trim().toString().toDouble()

        if (amount == 0.0) {
            baseContext.toast(resources.getString(R.string.amount_must_be_nonzero))
            return
        }

        val wallet = Neowallet.generateFromWIF(coldStorageWIF)
        scanButton.isEnabled = false
        scanButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
        Log.d("NODEURL", PersistentStore.getNodeURL())
        NeoNodeRPC(PersistentStore.getNodeURL()).sendAssetTransaction(wallet!!, this.selectedAsset, amount, Account.getWallet()?.address!!, null) {
            runOnUiThread {
                val error = it.second
                val success = it.first
                if (success == true) {
                    baseContext!!.toast(resources.getString(R.string.sent_successfully))
                    Handler().postDelayed(Runnable {
                        finish()
                    }, 1000)
                } else {
                }
            }
        }
    }

    private fun toggleAsset() {
        if (selectedAsset == NeoNodeRPC.Asset.NEO) {
            selectedAsset = NeoNodeRPC.Asset.GAS
            topupAmountTextView.setRawInputType(InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL or InputType.TYPE_NUMBER_FLAG_SIGNED)
            topupAmountTextView.keyListener = DigitsKeyListener.getInstance("0123456789.")
        } else {
            selectedAsset = NeoNodeRPC.Asset.NEO
            topupAmountTextView.setRawInputType(InputType.TYPE_CLASS_NUMBER)
            topupAmountTextView.keyListener = DigitsKeyListener.getInstance("0123456789")
            if (topupAmountTextView.text.trim().count() > 0) {
                var amount = topupAmountTextView.text.trim().toString().toDouble()
                topupAmountTextView.setText(Math.round(amount).toString())
            }
        }
        assetTextView.text = selectedAsset.name.toUpperCase()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents == null) {
            Toast.makeText(this, resources.getString(R.string.cancelled), Toast.LENGTH_LONG).show()
        } else {
            val sharedSecretPieceOne = Account.getColdStorageKeyFragmentOnDevice()
            val sharedSecretPieceTwo = result.contents
            try {
                coldStorageWIF = Neowallet.recoverFromSharedSecret(sharedSecretPieceOne.hexStringToByteArray(), sharedSecretPieceTwo.hexStringToByteArray())
                scanButton.text = resources.getString(R.string.confirm)
                scanButton.setOnClickListener { finishTransaction() }
            } catch (error: Error) {

            }

        }
    }
}
