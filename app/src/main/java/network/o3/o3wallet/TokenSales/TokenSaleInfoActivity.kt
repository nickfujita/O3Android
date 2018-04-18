package network.o3.o3wallet.TokenSales

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.CardView
import android.text.InputType
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.view.View
import android.widget.*
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import network.o3.o3wallet.API.O3.TokenSale
import network.o3.o3wallet.R
import com.bumptech.glide.Glide
import com.google.common.io.Resources
import kotlinx.android.synthetic.main.wallet_activity_send.*
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.O3.AcceptingAsset
import network.o3.o3wallet.afterTextChanged
import org.jetbrains.anko.backgroundColor
import org.jetbrains.anko.sdk15.coroutines.onClick
import org.jetbrains.anko.sdk15.coroutines.textChangedListener
import org.jetbrains.anko.textColor
import org.w3c.dom.Text


class TokenSaleInfoActivity : AppCompatActivity() {
    lateinit var tokenSale: TokenSale
    lateinit var gasInfo: AcceptingAsset
    lateinit var neoInfo: AcceptingAsset
    lateinit var selectedAsset: AcceptingAsset
    private lateinit var footerView: View
    private lateinit var headerView: View
    private lateinit var amountEditText: EditText
    private lateinit var participateButton: Button

    var priorityEnabled = false


    fun initiateAssetSelectorCards() {
        val gasCard = footerView.findViewById<CardView>(R.id.gasAssetCardView)
        val neoCard = footerView.findViewById<CardView>(R.id.neoAssetCardView)

        val gasCardTitleTextView = footerView.findViewById<TextView>(R.id.gasCardTitleTextView)
        val neoCardTitleTextView = footerView.findViewById<TextView>(R.id.neoCardTitleTextView)
        gasCardTitleTextView.text = "Use GAS"
        neoCardTitleTextView.text = "Use NEO"

        val gasCardDescriptionTextView = footerView.findViewById<TextView>(R.id.gasCardDecriptionTextView)
        val neoCardDescriptionTextView = footerView.findViewById<TextView>(R.id.neoCardDescriptionTextView)
        gasCardDescriptionTextView.text = "1 GAS = " + gasInfo.basicRate + " " + tokenSale.symbol
        neoCardDescriptionTextView.text = "1 NEO = " + neoInfo.basicRate + " " + tokenSale.symbol

        neoCardTitleTextView.textColor = resources.getColor(R.color.colorPrimary)
        neoCardDescriptionTextView.textColor = resources.getColor(R.color.colorPrimary)

        gasCardTitleTextView.textColor = resources.getColor(R.color.colorDisabledButton)
        gasCardDescriptionTextView.textColor = resources.getColor(R.color.colorDisabledButton)

        selectedAsset = neoInfo

        gasCard.setOnClickListener {
            gasCardTitleTextView.textColor = resources.getColor(R.color.colorPrimary)
            gasCardDescriptionTextView.textColor = resources.getColor(R.color.colorPrimary)

            neoCardTitleTextView.textColor = resources.getColor(R.color.colorDisabledButton)
            neoCardDescriptionTextView.textColor = resources.getColor(R.color.colorDisabledButton)

            selectedAsset = gasInfo
            amountEditText.text = SpannableStringBuilder("")
            amountEditText.inputType = InputType.TYPE_NUMBER_FLAG_DECIMAL
        }

        neoCard.setOnClickListener {
            neoCardTitleTextView.textColor = resources.getColor(R.color.colorPrimary)
            neoCardDescriptionTextView.textColor = resources.getColor(R.color.colorPrimary)

            gasCardTitleTextView.textColor = resources.getColor(R.color.colorDisabledButton)
            gasCardDescriptionTextView.textColor = resources.getColor(R.color.colorDisabledButton)

            selectedAsset = neoInfo
            amountEditText.text = SpannableStringBuilder("")
            amountEditText.inputType = InputType.TYPE_CLASS_NUMBER
        }
    }

    fun updateTokenRecieveAmount() {
        val doubleValue = amountEditText.text.toString().toDoubleOrNull()
        val recieveAmountTextView = footerView.findViewById<TextView>(R.id.tokenSaleRecieveAmountTextView)
        if (doubleValue != null) {
            recieveAmountTextView.text = (doubleValue * selectedAsset.basicRate).toString() + " " + tokenSale.symbol
            participateButton.isEnabled = true
            participateButton.backgroundColor = resources.getColor(R.color.colorPrimary)
        } else if (amountEditText.text.toString() == "") {
            recieveAmountTextView.text = "0" + " " + tokenSale.symbol
            participateButton.isEnabled = false
            participateButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
        }
    }

    fun initiatePartcipationEditText() {
        amountEditText.afterTextChanged {
            updateTokenRecieveAmount()
        }
    }

    fun initiateParticipateButton() {
        participateButton = footerView.findViewById(R.id.tokenSaleInfoParticipateButton)
        participateButton.isEnabled = false
        participateButton.backgroundColor = resources.getColor(R.color.colorDisabledButton)
        participateButton.setOnClickListener {
            val tokenSaleReviewIntent = Intent(this, TokenSaleReviewActivity::class.java)
            val sendAssetAmount = amountEditText.text.toString().toDouble()

            tokenSaleReviewIntent.putExtra("bannerURL", tokenSale.imageURL)
            tokenSaleReviewIntent.putExtra("assetSendAmount", sendAssetAmount)
            tokenSaleReviewIntent.putExtra("assetSendSymbol", selectedAsset.asset.toUpperCase())
            val assetID = if (selectedAsset.asset.toUpperCase() == "NEO") {
                NeoNodeRPC.Asset.NEO.assetID()
            } else {
                NeoNodeRPC.Asset.GAS.assetID()
            }

            tokenSaleReviewIntent.putExtra("assetSendId", assetID)
            tokenSaleReviewIntent.putExtra("assetReceiveSymbol", tokenSale.symbol.toUpperCase())
            tokenSaleReviewIntent.putExtra("assetReceiveAmount", sendAssetAmount * selectedAsset.basicRate)
            tokenSaleReviewIntent.putExtra("assetReceiveContractHash", tokenSale.scriptHash)
            tokenSaleReviewIntent.putExtra("withPriority", priorityEnabled)
            startActivity(tokenSaleReviewIntent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tokensale_info_activity)

        val tokenJSON = intent.getStringExtra("TOKENSALE_JSON")
        tokenSale = Gson().fromJson(tokenJSON)
        gasInfo = tokenSale.acceptingAssets.find { it.asset.toUpperCase() == "GAS" }!!
        neoInfo = tokenSale.acceptingAssets.find { it.asset.toUpperCase() == "NEO" }!!


        val listView = findViewById<ListView>(R.id.tokenInfoListView)
        listView.adapter = TokenSaleInfoAdapter(this)
        (listView.adapter as TokenSaleInfoAdapter).setData(tokenSale)

        headerView = layoutInflater.inflate(R.layout.tokensale_info_header, null)
        val bannerImageView = headerView.findViewById<ImageView>(R.id.tokensaleBannerView)
        Glide.with(this).load(tokenSale.imageURL).into(bannerImageView);
        footerView = layoutInflater.inflate(R.layout.tokensale_info_footer, null)

        amountEditText = footerView.findViewById<EditText>(R.id.tokenSaleParticipationAmountEditText)
        footerView.findViewById<TextView>(R.id.tokenSaleRecieveAmountTextView).text = "0" + tokenSale.symbol

        initiateParticipateButton()
        initiateAssetSelectorCards()
        initiatePartcipationEditText()
        listView.addHeaderView(headerView)
        listView.addFooterView(footerView)

    }
}
