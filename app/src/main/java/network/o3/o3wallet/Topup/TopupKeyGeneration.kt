package network.o3.o3wallet.Topup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import kotlinx.android.synthetic.main.topup_activity_topup_key_generation.*
import network.o3.o3wallet.R

class TopupKeyGeneration : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_topup_key_generation)

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
