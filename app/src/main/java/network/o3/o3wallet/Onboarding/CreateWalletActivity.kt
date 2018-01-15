package network.o3.o3wallet.Onboarding

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import net.glxn.qrgen.android.QRCode
import network.o3.o3wallet.Account
import network.o3.o3wallet.R
import network.o3.o3wallet.SelectingBestNode
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class CreateWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity_create_wallet)
        val startButton = findViewById<Button>(R.id.StartButton)
        startButton.setOnClickListener { startButtonTapped() }
        initTextViews()
    }

    fun startButtonTapped() {
        alert (resources.getString(R.string.alert_warning)) {
            yesButton {
                alert(resources.getString(R.string.warning_confirmation)) {
                    yesButton {
                        val intent = Intent(this@CreateWalletActivity, SelectingBestNode::class.java)
                        startActivity(intent)
                    }
                    noButton {

                    }
                }.show()
            }
        }.show()
    }

    fun initTextViews() {
        val addressTextView = findViewById<TextView>(R.id.addressTextView)
        val wifTextView = findViewById<TextView>(R.id.wifTextView)
        val qrView = findViewById<ImageView>(R.id.qrView)

        addressTextView.text = Account.getWallet()?.address
        wifTextView.text = Account.getWallet()?.wif
        val bitmap = QRCode.from(Account.getWallet()!!.wif).withSize(1000, 1000).bitmap()
        qrView.setImageBitmap(bitmap)
    }
}
