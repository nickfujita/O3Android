package network.o3.o3wallet.TokenSales

import android.content.Intent
import android.opengl.Visibility
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import network.o3.o3wallet.R
import org.w3c.dom.Text

class TokenSaleReviewActivity : AppCompatActivity() {

    private lateinit var bannerURL: String
    private lateinit var assetSendSymbol: String
    private lateinit var assetSendId: String
    private lateinit var assetReceiveSymbol: String
    private lateinit var assetReceiveContractHash: String
    private lateinit var tokenSaleName: String

    private var assetSendAmount: Double = 0.0
    private var assetReceiveAmount: Double = 0.0
    private var priorityEnabled: Boolean = false

    fun initiateViews() {
        val bannerView = findViewById<ImageView>(R.id.tokenSaleReviewBannerImageView)
        val sendAmountView = findViewById<TextView>(R.id.tokenSaleReviewSendAmountTextView)
        val receiveAmountTextView = findViewById<TextView>(R.id.tokenSaleReviewReceiveAmountTextView)
        val priorityTextView = findViewById<TextView>(R.id.tokenSaleReviewPriorityTextView)

        Glide.with(this).load(bannerURL).into(bannerView)
        sendAmountView.text = assetSendAmount.toString() + " " + assetSendSymbol
        receiveAmountTextView.text = assetReceiveAmount.toString() + " " + assetReceiveSymbol
        if (!priorityEnabled) {
            priorityTextView.visibility = View.GONE
        }
    }

    fun initiateParticipateButton() {
        val button = findViewById<Button>(R.id.tokenSaleReviewParticipateButton)
        button.setOnClickListener {
            val intent = Intent(this, TokenSaleReceiptActivity::class.java)
            intent.putExtra("assetSendSymbol", assetSendSymbol)
            intent.putExtra("assetSendAmount", assetSendAmount)
            intent.putExtra("assetReceiveSymbol", assetReceiveSymbol)
            intent.putExtra("assetReceiveAmount", assetReceiveAmount)
            intent.putExtra("priorityEnabled", priorityEnabled)
            intent.putExtra("transactionID", "INSERT TRANSACTION ID")
            intent.putExtra("tokenSaleName", tokenSaleName)
            startActivity(intent)
        }
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
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tokensale_review_activity)

        parseIntent()
        initiateViews()
        initiateParticipateButton()
    }
}
