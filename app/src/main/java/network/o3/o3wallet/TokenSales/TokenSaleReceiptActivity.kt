package network.o3.o3wallet.TokenSales

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.TextView
import network.o3.o3wallet.R

class TokenSaleReceiptActivity : AppCompatActivity() {
    private lateinit var tokenSaleName: String
    private lateinit var txID: String
    private lateinit var assetSendSymbol: String
    private lateinit var assetReceiveSymbol: String

    private var assetSendAmount: Double = 0.0
    private var assetReceiveAmount: Double = 0.0
    private var priorityEnabled: Boolean = false

    fun setRecieptValues() {
        val txidview = findViewById<TextView>(R.id.receiptTxIdValueTextView)
        txidview.text = txID

        val tokenSaleTextView = findViewById<TextView>(R.id.receiptSaleNameValueTextView)
        tokenSaleTextView.text = tokenSaleName

        val assetSendTextView = findViewById<TextView>(R.id.receiptSendingValueTextView)
        assetSendTextView.text = assetSendAmount.toString() + " " + assetSendSymbol

        val assetReceiveTextView = findViewById<TextView>(R.id.receiptForValueTextView)
        assetReceiveTextView.text = assetSendAmount.toString() + assetReceiveSymbol

        if (!priorityEnabled) {
            findViewById<TextView>(R.id.receiptPriorityValueTextView).visibility = View.INVISIBLE
            findViewById<TextView>(R.id.receiptPriorityLabelTextView).visibility = View.INVISIBLE
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tokensale_receipt_activity)

        assetSendSymbol = intent.getStringExtra("assetSendSymbol")
        assetSendAmount = intent.getDoubleExtra("assetSendAmount", 0.0)
        assetReceiveSymbol = intent.getStringExtra("assetReceiveSymbol")
        assetReceiveAmount = intent.getDoubleExtra("assetReceiveAmount", 0.0)
        priorityEnabled = intent.getBooleanExtra("priorityEnabled", false)
        txID = intent.getStringExtra("transactionID")
        tokenSaleName = intent.getStringExtra("tokenSaleName")

        setRecieptValues()
    }
}
