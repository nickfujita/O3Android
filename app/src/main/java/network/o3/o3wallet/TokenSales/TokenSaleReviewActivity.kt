package network.o3.o3wallet.TokenSales

import android.content.Intent
import android.net.Uri
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.Account
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.backgroundColor
import org.w3c.dom.Text
import java.text.DecimalFormat

class TokenSaleReviewActivity : AppCompatActivity() {

    private lateinit var bannerURL: String
    private lateinit var assetSendSymbol: String
    private lateinit var assetSendId: String
    private lateinit var assetReceiveSymbol: String
    private lateinit var assetReceiveContractHash: String
    private lateinit var tokenSaleName: String
    private lateinit var tokenSaleWebURL: String

    private var assetSendAmount: Double = 0.0
    private var assetReceiveAmount: Double = 0.0
    private var priorityEnabled: Boolean = false

    private lateinit var participateButton: Button

    fun initiateViews(whitelisted: Boolean) {
        val bannerView = findViewById<ImageView>(R.id.tokenSaleReviewBannerImageView)
        val sendAmountView = findViewById<TextView>(R.id.tokenSaleReviewSendAmountTextView)
        val receiveAmountTextView = findViewById<TextView>(R.id.tokenSaleReviewReceiveAmountTextView)
        val priorityTextView = findViewById<TextView>(R.id.tokenSaleReviewPriorityTextView)
        val whiteListFloatingActionButton = findViewById<FloatingActionButton>(R.id.whiteListFloatingActionButton)
        val whiteListErrorTextView = findViewById<TextView>(R.id.whiteListErrorTextView)
        val issuerAgreementCheckbox = findViewById<CheckBox>(R.id.issuerDisclaimerCheckbox)
        val o3AgreementCheckbox = findViewById<CheckBox>(R.id.o3DisclaimerCheckbox)

        Glide.with(this).load(bannerURL).into(bannerView)
        val df = DecimalFormat()
        if (assetSendSymbol == "NEO") {
            df.maximumFractionDigits = 0
        }  else {
            df.maximumFractionDigits = 8
        }
        sendAmountView.text = df.format(assetSendAmount) + " " + assetSendSymbol
        df.maximumFractionDigits = 8
        receiveAmountTextView.text = df.format(assetReceiveAmount) + " " + assetReceiveSymbol
        if (!priorityEnabled) {
            priorityTextView.visibility = View.GONE
        }

        o3AgreementCheckbox.setOnClickListener {
            if (o3AgreementCheckbox.isChecked && issuerAgreementCheckbox.isChecked) {
                participateButton.isEnabled = true
                participateButton.backgroundColor = resources.getColor(R.color.colorPrimary)
            } else {
                participateButton.isEnabled = false
                participateButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
            }
        }

        issuerAgreementCheckbox.setOnClickListener {
            if (o3AgreementCheckbox.isChecked && issuerAgreementCheckbox.isChecked) {
                participateButton.isEnabled = true
                participateButton.backgroundColor = resources.getColor(R.color.colorPrimary)
            } else {
                participateButton.isEnabled = false
                participateButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
            }
        }

        //TODO: READD WHITELISTING WHEN SHIPPING
        /*if (!whitelisted) {
            whiteListFloatingActionButton.visibility = View.VISIBLE
            whiteListFloatingActionButton.setOnClickListener {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(tokenSaleWebURL))
                startActivity(browserIntent)
            }
            whiteListErrorTextView.text = "It looks like you're not whitelisted for this token sale. " +
                    "You can use the issuers website to figure out how to get whitelisted or contact the team " +
                    "if you think this is a mistake"
            issuerAgreementCheckbox.visibility = View.GONE
            o3AgreementCheckbox.visibility = View.GONE
        }*/
    }

    fun initiateParticipateButton(whitelisted: Boolean) {
        participateButton = findViewById<Button>(R.id.tokenSaleReviewParticipateButton)
        participateButton.isEnabled = false
        participateButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
        participateButton.setOnClickListener {
            val intent = Intent(this, TokenSaleReceiptActivity::class.java)
            intent.putExtra("assetSendSymbol", assetSendSymbol)
            intent.putExtra("assetSendAmount", assetSendAmount)
            intent.putExtra("assetReceiveSymbol", assetReceiveSymbol)
            intent.putExtra("assetReceiveAmount", assetReceiveAmount)
            intent.putExtra("priorityEnabled", priorityEnabled)
            intent.putExtra("transactionID", "INSERT TRANSACTION ID")
            intent.putExtra("tokenSaleName", tokenSaleName)
            intent.putExtra("tokenSaleWebURL", tokenSaleWebURL)
            startActivity(intent)
        }
        //TODO: READD WHITELISTING WHEN SHIPPING
        /*if (!whitelisted) {
            button.visibility = View.GONE
        }*/
    }

    fun parseIntent() {
        bannerURL = intent.getStringExtra("bannerURL")
        assetSendSymbol = intent.getStringExtra("assetSendSymbol")
        assetSendAmount = intent.getDoubleExtra("assetSendAmount", 0.0)
        assetSendId = intent.getStringExtra("assetSendId")
        assetReceiveSymbol = intent.getStringExtra("assetReceiveSymbol")
        assetReceiveContractHash = intent.getStringExtra("assetReceiveContractHash")
        assetReceiveAmount = intent.getDoubleExtra("assetReceiveAmount", 0.0)
        priorityEnabled = intent.getBooleanExtra("priorityEnabled", false)
        tokenSaleName = intent.getStringExtra("tokenSaleName")
        tokenSaleWebURL = intent.getStringExtra("tokenSaleWebURL")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        parseIntent()
        NeoNodeRPC(PersistentStore.getNodeURL()).getWhiteListStatus(assetReceiveContractHash, Account.getWallet()?.address!!) {
            runOnUiThread {
                if (it.second != null) {
                    setContentView(R.layout.tokensale_review_activity)
                    initiateViews(false)
                    initiateParticipateButton(false)

                } else {
                    initiateViews(it.first!!)
                    initiateParticipateButton(it.first!!)
                }
            }
        }
    }
}
