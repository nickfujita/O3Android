package network.o3.o3wallet.Topup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.onboarding_activity_create_wallet.*
import kotlinx.android.synthetic.main.topup_tutorial_activity_tutorial_private_key.*
import neowallet.Neowallet
import network.o3.o3wallet.R
import network.o3.o3wallet.toHex
import org.jetbrains.anko.alert

//For Testing Info: L123PcmTdPXLkYuNTsTYnFiVBhkduLDoKybuL6AHJo6MRRFxANDW
// Piece One: FC26C00B7CFC52832925CC7C19815EB768531718335160DF03471B8FE6668BA5C0C4C4147493DC5C4D8FC10421B9AF223D4A67F4
// Piece Two: 371FCD43084613E1FEBA6B2C8FF223A72C33D2DBD4687B5FC0368BA94818C1E0461835B73C67606044B4C3DFB49F8FCCB946020A
class TutorialPrivateKey : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_tutorial_activity_tutorial_private_key)

        coldStorageScanButton.setOnClickListener {
            val integrator = IntentIntegrator(this)
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
            integrator.setPrompt(resources.getString(R.string.scan_prompt_cold_storage))
            integrator.setOrientationLocked(false)
            integrator.initiateScan()
        }

        generateFragmentsButton.setOnClickListener { encryptButtonTapped() }
    }

    fun encryptButtonTapped() {
        try {
            //TODO: READD COLDSTORAGE WIF
            val tempWallet = Neowallet.generateFromWIF(coldStorageWifTextView.text.toString())
            val intent = Intent(this, TopupKeyGeneration::class.java)
            val sharedSecret = Neowallet.generateShamirSharedSecret(tempWallet.wif)
            intent.putExtra("SecretPieceOne", sharedSecret.first.toHex())
            intent.putExtra("SecretPieceTwo", sharedSecret.second.toHex())
            intent.putExtra("Address", tempWallet.address)
            startActivity(intent)
        } catch (e: Exception) {
            alert ("Invalid WIF Key Please try again").show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null && result.contents == null) {
            Toast.makeText(this, resources.getString(R.string.cancelled), Toast.LENGTH_LONG).show()
        } else {
            coldStorageWifTextView.setText(result.contents)
        }
    }
}
