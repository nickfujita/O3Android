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
import org.jetbrains.anko.alert

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
            val tempWallet = Neowallet.generateFromWIF(coldStorageWifTextView.text.toString())
            val intent = Intent(this, TopupKeyGeneration::class.java)
            val sharedSecret = Neowallet.generateShamirSharedSecret(tempWallet.wif)
            intent.putExtra("SecretFragmentOne", sharedSecret.first)
            intent.putExtra("SecretFragmentTwo", sharedSecret.second)
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
