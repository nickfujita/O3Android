package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.robinhood.spark.SparkView
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.API.O3.Portfolio
import network.o3.o3wallet.R
import network.o3.o3wallet.formattedUSDString
import android.arch.lifecycle.Observer
import com.robinhood.spark.animation.MorphSparkAnimator
import network.o3.o3wallet.API.O3.PriceHistory
import network.o3.o3wallet.formattedBTCString

class AssetGraph : AppCompatActivity() {
    var selectedButton: Button? = null
    var symbol: String? = null
    var unscrubbedDisplayAmount = 0.0
    var assetGraphModel: AssetGraphViewModel? = null
    var chartDataAdapter = PortfolioDataAdapter(FloatArray(0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset_graph)
        symbol = intent.getStringExtra("SYMBOL")
        assetGraphModel = ViewModelProviders.of(this).get(AssetGraphViewModel::class.java)
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        val sparkView = findViewById<SparkView>(R.id.sparkview)
        sparkView.sparkAnimator = MorphSparkAnimator()
        sparkView.adapter = chartDataAdapter
        loadGraph(true)

        sparkView.scrubListener = SparkView.OnScrubListener { value ->
            if (value == null) { //return to original state
                if (assetGraphModel?.getCurrency() == AssetGraphViewModel.Currency.USD) {
                    priceView.text = assetGraphModel?.getLatestPrice()?.averageUSD?.formattedUSDString()
                } else {
                    priceView.text = assetGraphModel?.getLatestPrice()?.averageBTC?.formattedBTCString()
                }
                return@OnScrubListener
            } else {
                if (assetGraphModel?.getCurrency() == AssetGraphViewModel.Currency.USD) {
                    priceView?.text = (value as Float).toDouble().formattedUSDString()
                } else {
                    priceView?.text = (value as Float).toDouble().formattedBTCString()
                }
            }
        }
        initiateIntervalButtons()

        priceView.setOnClickListener {
            if (assetGraphModel?.getCurrency() == AssetGraphViewModel.Currency.USD) {
                assetGraphModel?.setCurrency(AssetGraphViewModel.Currency.BTC)
            } else {
                assetGraphModel?.setCurrency(AssetGraphViewModel.Currency.USD)
            }
            loadGraph(true)
        }
    }

    fun initiateIntervalButtons() {
        val fiveMinButton = findViewById<Button>(R.id.fiveMinInterval)
        val fifteenMinButton = findViewById<Button>(R.id.fifteenMinuteInterval)
        val thirtyMinButton = findViewById<Button>(R.id.thirtyMinuteInterval)
        val sixtyMinButton = findViewById<Button>(R.id.sixtyMinuteInterval)
        val oneDayButton = findViewById<Button>(R.id.oneDayInterval)
        val allButton = findViewById<Button>(R.id.allInterval)

        selectedButton = fifteenMinButton

        fiveMinButton.setOnClickListener { tappedIntervalButton(fiveMinButton) }
        fifteenMinButton.setOnClickListener { tappedIntervalButton(fifteenMinButton) }
        thirtyMinButton.setOnClickListener { tappedIntervalButton(thirtyMinButton) }
        sixtyMinButton.setOnClickListener { tappedIntervalButton(sixtyMinButton) }
        oneDayButton.setOnClickListener { tappedIntervalButton(oneDayButton) }
        allButton.setOnClickListener { tappedIntervalButton(allButton) }
    }

    fun loadGraph(refresh: Boolean) {
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        assetGraphModel?.getHistoryFromModel(refresh)?.observe(this, Observer<PriceHistory> { data ->
            chartDataAdapter.setData(assetGraphModel?.getPriceFloats())
            if (assetGraphModel?.getCurrency() == AssetGraphViewModel.Currency.USD) {
                priceView.text = assetGraphModel?.getLatestPrice()?.averageUSD?.formattedUSDString()
            } else {
                priceView.text = assetGraphModel?.getLatestPrice()?.averageBTC?.formattedBTCString()
            }
        })
    }

    fun tappedIntervalButton(button: Button) {
        selectedButton?.setBackgroundResource(R.drawable.bottom_unselected)
        button.setBackgroundResource(R.drawable.bottom_selected)
        selectedButton = button
        assetGraphModel?.setInterval(button.tag.toString().toInt())
        loadGraph(true)
    }
}
