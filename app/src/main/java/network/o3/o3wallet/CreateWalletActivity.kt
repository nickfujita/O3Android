package network.o3.o3wallet

import android.content.Intent
import android.content.res.Resources
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.alert
import org.jetbrains.anko.noButton
import org.jetbrains.anko.yesButton


class CreateWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)
        val startButton = findViewById<Button>(R.id.StartButton)
        startButton.setOnClickListener { startButtonTapped() }
        initTextViews()
    }

    fun startButtonTapped() {
        alert ( "Your private key is the most important piece of information in cryptocurrency applications.\n We " +
                "will save an encrypted version on your device, but please make sure to write down this " +
                "private key in another secure location so that you may retrieve your funds in case something " +
                "happens to your device." ) {
            yesButton {
                alert("I confirm that I have backed up my private key in another secure location") {
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
