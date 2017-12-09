package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.robinhood.spark.SparkView
import android.arch.lifecycle.Observer
import com.robinhood.spark.animation.MorphSparkAnimator
import network.o3.o3wallet.*
import network.o3.o3wallet.API.O3.PriceHistory

/**
 * Created by drei on 12/8/17.
 */

class AssetGraph : AppCompatActivity() {
    private var selectedButton: Button? = null
    private var symbol: String? = null
    private var assetGraphModel: AssetGraphViewModel? = null
    private var chartDataAdapter = PortfolioDataAdapter(FloatArray(0))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset_graph)
        symbol = intent.getStringExtra("SYMBOL")
        assetGraphModel = ViewModelProviders.of(this).get(AssetGraphViewModel::class.java)

        initiateGraph()
        initiateIntervalButtons()
        initiatePriceView()
    }

    private fun initiateGraph() {
        val sparkView = findViewById<SparkView>(R.id.sparkview)
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        sparkView.sparkAnimator = MorphSparkAnimator()
        sparkView.adapter = chartDataAdapter
        loadGraph(true)

        sparkView.scrubListener = SparkView.OnScrubListener { value ->
            if (value == null) { //return to original state
                priceView.text = assetGraphModel?.getLatestPriceFormattedString()
                return@OnScrubListener
            } else {
                priceView?.text = (value as Float).toDouble().formattedCurrencyString(assetGraphModel!!.getCurrency())
            }
        }
    }

    private fun initiatePriceView() {
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        priceView.setOnClickListener {
            if (assetGraphModel?.getCurrency() == CurrencyType.USD) {
                assetGraphModel?.setCurrency(CurrencyType.USD)
            } else {
                assetGraphModel?.setCurrency(CurrencyType.USD)
            }
            loadGraph(true)
        }
    }

    private fun initiateIntervalButtons() {
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

    private fun loadGraph(refresh: Boolean) {
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        assetGraphModel?.getHistoryFromModel(refresh)?.observe(this, Observer<PriceHistory> { data ->
            chartDataAdapter.setData(assetGraphModel?.getPriceFloats())
            priceView.text = assetGraphModel?.getLatestPriceFormattedString()
        })
    }

    private fun tappedIntervalButton(button: Button) {
        selectedButton?.setBackgroundResource(R.drawable.bottom_unselected)
        button.setBackgroundResource(R.drawable.bottom_selected)
        selectedButton = button
        assetGraphModel?.setInterval(button.tag.toString().toInt())
        loadGraph(true)
    }
}
