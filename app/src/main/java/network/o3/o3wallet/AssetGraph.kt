package network.o3.o3wallet

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import com.robinhood.spark.SparkView
import network.o3.o3wallet.API.O3.O3API

class AssetGraph : AppCompatActivity() {
    var selectedButton: Button? = null
    var symbol: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_asset_graph)
        symbol = intent.getStringExtra("SYMBOL")
        val sparkView = findViewById<SparkView>(R.id.sparkview)
        val priceView = findViewById<TextView>(R.id.currentPriceTextView)
        sparkView.setScrubListener(SparkView.OnScrubListener {
            value -> priceView.text = value.toString()
        })
        initiateIntervalButtons()
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

    fun tappedIntervalButton(button: Button) {
        selectedButton?.setBackgroundResource(R.drawable.bottom_unselected)
        button.setBackgroundResource(R.drawable.bottom_selected)
        selectedButton = button
        O3API().getPriceHistory(symbol!!, Integer.parseInt(button.tag.toString())) {
            val portfolioGraph = findViewById<SparkView>(R.id.sparkview)
            val data = it.first?.data?.map { it.averageUSD }?.toTypedArray()!!
            var floats = FloatArray(data.count())
            for (i in data.indices) {
                floats[i] = data[i].toFloat()
            }
            portfolioGraph?.setAdapter(PortfolioDataAdapter(floats))
        }
    }
}
