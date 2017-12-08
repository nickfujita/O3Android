package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.robinhood.spark.SparkView
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.API.O3.PriceData
import network.o3.o3wallet.API.O3.PriceHistory
import network.o3.o3wallet.R
import java.util.*

/**
 * Created by drei on 12/8/17.
 */

class AssetGraphViewModel: ViewModel() {
    private var history: MutableLiveData<PriceHistory>? = null
    private var symbol = "NEO"
    private var interval = 15
    private var currency = Currency.USD
    private var latestPrice: PriceData? = null

    enum class Currency {
        BTC, USD
    }

    fun getCurrency(): Currency {
        return currency
    }

    fun setCurrency(currency: Currency) {
        this.currency = currency
    }

    fun getLatestPrice(): PriceData {
        return latestPrice!!
    }

    fun setInterval(interval: Int) {
        this.interval = interval
    }

    fun getHistoryFromModel(refresh: Boolean): LiveData<PriceHistory> {
        if (history == null || refresh) {
            history = MutableLiveData()
            loadHistory()
        }
        return history!!
    }

    fun getPriceFloats(): FloatArray {
        val data = when (currency) {
            Currency.USD -> history?.value?.data?.map { it.averageUSD }?.toTypedArray()!!
            Currency.BTC -> history?.value?.data?.map { it.averageBTC }?.toTypedArray()!!
        }

        var floats = FloatArray(data.count())
        for (i in data.indices) {
            floats[i] = data[i].toFloat()
        }
        return floats.reversedArray()
    }

    fun loadHistory() {
        O3API().getPriceHistory(symbol, interval) {
            if (it?.second != null) return@getPriceHistory
            latestPrice = it?.first?.data?.first()!!
            history?.postValue(it.first!!)
        }
    }
}