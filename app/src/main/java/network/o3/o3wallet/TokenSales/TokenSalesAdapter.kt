package network.o3.o3wallet.TokenSales

import android.content.Intent
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import network.o3.o3wallet.API.O3.TokenSale
import network.o3.o3wallet.R
import org.jetbrains.anko.find

/**
 * Created by drei on 4/17/18.
 */

class TokenSalesAdapter(private var tokensales: ArrayList<TokenSale>): RecyclerView.Adapter<TokenSalesAdapter.TokenSaleHolder>() {

    fun setData(tokenSales: ArrayList<TokenSale>) {
        this.tokensales = tokenSales
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return tokensales.count()
    }

    override fun onBindViewHolder(holder: TokenSaleHolder?, position: Int) {
        holder?.bindTokenSale(tokensales[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): TokenSaleHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        val view = layoutInflater.inflate(R.layout.tokensales_listing_row, parent, false)
        return TokenSaleHolder(view)
    }

    class TokenSaleHolder(v: View) : RecyclerView.ViewHolder(v), View.OnClickListener {
        private var view: View = v
        private var tokenSale: TokenSale? = null

        init {
            v.setOnClickListener(this)
        }

        override fun onClick(v: View) {
            if (tokenSale != null) {
                val tokenSaleInfoIntent = Intent(view.context, TokenSaleInfoActivity::class.java)
                tokenSaleInfoIntent.putExtra("TOKENSALE_JSON", Gson().toJson(tokenSale!!))
                view.context.startActivity(tokenSaleInfoIntent)
            }
        }

        companion object {
            private val TOKENSALE_KEY = "TOKENSALE"
        }

        fun bindTokenSale(tokenSale: TokenSale?) {
            this.tokenSale = tokenSale!!
            val daysLeftTextView = view.find<TextView>(R.id.tokenSaleDaysLeftTextView)
            val coinNameTextView = view.find<TextView>(R.id.tokenSaleCoinNameTextView)
            val coinDescriptionTextView = view.find<TextView>(R.id.tokenSaleDescriptionTextView)
            val logoImageView = view.find<ImageView>(R.id.tokenSaleSquareImageView)

            daysLeftTextView.text = ((tokenSale.endTime - (System.currentTimeMillis()/1000)) / 3600 / 24).toString() + "Days Remaining"
            coinNameTextView.text = tokenSale.name
            coinDescriptionTextView.text = tokenSale.shortDescription
            Glide.with(view.context).load(tokenSale.squareLogoURL).into(logoImageView)
        }
    }
}
