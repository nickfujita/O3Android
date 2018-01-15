package network.o3.o3wallet.Topup

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.topup_activity_topup_second_fragment_info.*
import net.glxn.qrgen.android.QRCode
import network.o3.o3wallet.Account
import network.o3.o3wallet.R
import android.content.Intent
import network.o3.o3wallet.PersistentStore
import org.jetbrains.anko.backgroundColor


class TopupSecondFragmentInfo : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_topup_second_fragment_info)
        val secretPieceTwo = intent.getStringExtra("SecretPieceTwo")
        secretPieceTwoTextView.text = secretPieceTwo
        secretKeySavedCheckbox.text = resources.getString(R.string.save_fragment_confirm)
        val bitmap = QRCode.from(Account.getWallet()!!.wif).withSize(1000, 1000).bitmap()
        secretPieceQrCodeView.setImageBitmap(bitmap)

        secondFragmentDoneButton.isEnabled = false
        secondFragmentDoneButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
        secretKeySavedCheckbox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                secondFragmentDoneButton.isEnabled = true
                secondFragmentDoneButton.backgroundColor = resources.getColor(R.color.colorAccent)
            } else {
                secondFragmentDoneButton.isEnabled = false
                secondFragmentDoneButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
            }
        }

        secondFragmentDoneButton.setOnClickListener {
            val secretPieceOne = intent.getStringExtra("SecretPieceOne")
            val address = intent.getStringExtra("Address")
            Account.storeColdStorageKeyFragmentOnDevice(secretPieceOne)
            PersistentStore.setColdStorageVaultAddress(address)
            PersistentStore.setColdStorageEnabledStatus(true)
            val intent = Intent(this, TopupColdStorageBalanceActivity::class.java)
            startActivity(intent)
        }

        saveSecondFragmentButton.setOnClickListener {
            //TODO: SWITCH THIS TO SEND QR IMAGE
            val sendIntent = Intent()
            sendIntent.action = Intent.ACTION_SEND
            sendIntent.putExtra(Intent.EXTRA_TEXT, secretPieceTwo)
            sendIntent.type = "text/plain"
            startActivity(sendIntent)
        }
    }
}
