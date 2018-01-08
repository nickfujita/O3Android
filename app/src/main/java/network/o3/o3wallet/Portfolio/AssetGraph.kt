package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.ViewModelProviders
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import com.robinhood.spark.SparkView
import android.arch.lifecycle.Observer
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import com.robinhood.spark.animation.MorphSparkAnimator
import network.o3.o3wallet.*
import network.o3.o3wallet.API.O3.PriceHistory
import org.w3c.dom.Text

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
        title = resources.getString(R.string.price_history, symbol!!)
        assetGraphModel = ViewModelProviders.of(this).get(AssetGraphViewModel::class.java)

        initiateGraph()
        initiateIntervalButtons()
        initiatePriceView()
    }

    fun setHeaderToInitialValues() {
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        val percentView = findViewById<TextView>(R.id.percentChangeTextView)
        priceView.text = assetGraphModel?.getLatestPriceFormattedString()
        val percentChange = assetGraphModel?.getPercentChange()
        if (percentChange!! < 0) {
            percentView?.setTextColor(resources.getColor(R.color.colorLoss))
        } else {
            percentView?.setTextColor(resources.getColor(R.color.colorGain))
        }
        percentView.text = percentChange.formattedPercentString()
    }

    private fun initiateGraph() {
        val sparkView = findViewById<SparkView>(R.id.sparkview)
        sparkView.sparkAnimator = MorphSparkAnimator()
        sparkView.adapter = chartDataAdapter
        loadGraph(true)

        sparkView.scrubListener = SparkView.OnScrubListener { value ->
            if (value == null) { //return to original state
                setHeaderToInitialValues()
                return@OnScrubListener
            } else {
                val priceView = findViewById<TextView>(R.id.currentPriceTextView)
                val percentView = findViewById<TextView>(R.id.percentChangeTextView)
                val price = (value as Float).toDouble()
                val percentChange = ((price - assetGraphModel?.getInitialPrice()!!) /
                        assetGraphModel?.getInitialPrice()!! * 100)

                if (percentChange!! < 0) {
                    percentView?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    percentView?.setTextColor(resources.getColor(R.color.colorGain))
                }
                priceView.text = price.formattedCurrencyString(assetGraphModel!!.getCurrency())
                percentView.text = percentChange.formattedPercentString()
            }
        }
    }

    private fun initiatePriceView() {
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        priceView.setOnClickListener {
            if (assetGraphModel?.getCurrency() == CurrencyType.USD) {
                assetGraphModel?.setCurrency(CurrencyType.BTC)
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
        val percentView = findViewById<TextView>(R.id.percentChangeTextView)
        val progress = findViewById<ProgressBar>(R.id.progressBar)
        progress?.visibility = View.VISIBLE
        assetGraphModel?.getHistoryFromModel(refresh)?.observe(this, Observer<PriceHistory> { data ->
            progress?.visibility = View.GONE
            chartDataAdapter.setData(assetGraphModel?.getPriceFloats())
            priceView.text = assetGraphModel?.getLatestPriceFormattedString()

            val percentChange = assetGraphModel?.getPercentChange()
            if (percentChange!! < 0) {
                percentView?.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorLoss))
            } else {
                percentView?.setTextColor(ContextCompat.getColor(applicationContext,R.color.colorGain))
            }
            percentView.text = percentChange.formattedPercentString()
        })
    }

    fun tappedIntervalButton(button: Button) {
        selectedButton?.setTextAppearance(R.style.IntervalButtonText_NotSelected)
        button?.setTextAppearance(R.style.IntervalButtonText_Selected)
        selectedButton = button
        assetGraphModel?.setInterval(button.tag.toString().toInt())
        loadGraph(true)
    }
}
