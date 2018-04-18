package network.o3.o3wallet.TokenSales

import android.arch.lifecycle.Observer
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.RecyclerView
import network.o3.o3wallet.API.O3.TokenSale
import network.o3.o3wallet.Feed.FeaturesAdapter
import network.o3.o3wallet.R

class TokenSalesActivity : AppCompatActivity() {

    var model: TokenSalesViewModel? = null
    var tokenSales = ArrayList<TokenSale>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.tokensales_list_activity)

        val listingsView = findViewById<RecyclerView>(R.id.tokenSalesListingRecyclerView)
        listingsView?.adapter = TokenSalesAdapter(tokensales = tokenSales)

        model = TokenSalesViewModel()
        model?.getTokenSales(true)?.observe(this, Observer { tokenSales ->
            (listingsView.adapter as TokenSalesAdapter).setData(tokenSales?.live?.toCollection(ArrayList())!!)
        })

    }
}
