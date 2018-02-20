package network.o3.o3wallet.Topup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.InputType
import android.text.method.DigitsKeyListener
import android.widget.Button
import android.widget.Toast
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.topup_activity_send_amount.*
import neoutils.Neoutils.generateFromWIF
import neoutils.Neoutils.recoverFromSharedSecret
import neoutils.Wallet
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.Wallet.toast
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.image


class TopupSendAmountActivity : LocalizationActivity() {
    var coldStorageWIF: String? = null
    private var selectedAsset = NeoNodeRPC.Asset.NEO


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_send_amount)
        val scanButton = findViewById<Button>(R.id.scanButton)
        coldStorageAddressView.text = PersistentStore.getColdStorageVaultAddress()
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
        scanButton.isEnabled = false
        val wallet = generateFromWIF(coldStorageWIF)
        scanButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
        NeoNodeRPC(PersistentStore.getNodeURL()).sendNativeAssetTransaction(wallet!!, this.selectedAsset, amount, Account.getWallet()?.address!!, null) {
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
                coldStorageWIF = recoverFromSharedSecret(sharedSecretPieceOne.hexStringToByteArray(), sharedSecretPieceTwo.hexStringToByteArray())
                scanButton.text = resources.getString(R.string.confirm)
                lockImageView.image = resources.getDrawable(R.drawable.ic_lock_open_alt)
                scanButton.setOnClickListener { finishTransaction() }
            } catch (error: Error) {

            }

        }
    }
}
