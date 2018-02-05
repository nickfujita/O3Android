package network.o3.o3wallet.Topup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import kotlinx.android.synthetic.main.topup_activity_topup_key_generation.*
import network.o3.o3wallet.R

class TopupKeyGeneration : LocalizationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_topup_key_generation)
        val secretPieceOne = intent.getStringExtra("SecretPieceOne")
        val secretPieceTwo = intent.getStringExtra("SecretPieceTwo")
        val address = intent.getStringExtra("Address")
        continueEncryptionButton.setOnClickListener {
            val intent = Intent(this, TopupSecondFragmentInfo::class.java)
            intent.putExtra("SecretPieceTwo", secretPieceTwo)
            intent.putExtra("SecretPieceOne", secretPieceOne)
            intent.putExtra("Address", address)
            startActivity(intent)
        }
        Handler().postDelayed({ showCompletion() }, 3000)
    }

        fun showCompletion() {
            encryptingProgress.visibility = View.INVISIBLE
            encryptionProgressTextView.text = resources.getString(R.string.encryption_done)
            lockImage.visibility = View.VISIBLE
            encryptionInfoTextView.visibility = View.VISIBLE
            continueEncryptionButton.visibility = View.VISIBLE
        }
}
