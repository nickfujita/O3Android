package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.API.O3.PriceData
import network.o3.o3wallet.API.O3.PriceHistory
import network.o3.o3wallet.CurrencyType
import network.o3.o3wallet.formattedBTCString
import network.o3.o3wallet.formattedUSDString

/**
 * Created by drei on 12/8/17.
 */

class AssetGraphViewModel: ViewModel() {
    private var history: MutableLiveData<PriceHistory>? = null
    private var symbol = "NEO"
    private var interval = 15
    private var currency = CurrencyType.USD
    private var latestPrice: PriceData? = null


    fun getCurrency(): CurrencyType {
        return currency
    }

    fun setCurrency(currency: CurrencyType) {
        this.currency = currency
    }

    fun getLatestPriceFormattedString(): String {
        return if (currency == CurrencyType.BTC) {
            latestPrice?.averageBTC?.formattedBTCString()!!
        } else {
            latestPrice?.averageUSD?.formattedUSDString()!!
        }
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
            CurrencyType.USD -> history?.value?.data?.map { it.averageUSD }?.toTypedArray()!!
            CurrencyType.BTC -> history?.value?.data?.map { it.averageBTC }?.toTypedArray()!!
        }

        var floats = FloatArray(data.count())
        for (i in data.indices) {
            floats[i] = data[i].toFloat()
        }
        return floats.reversedArray()
    }

    private fun loadHistory() {
        O3API().getPriceHistory(symbol, interval) {
            if (it?.second != null) return@getPriceHistory
            latestPrice = it?.first?.data?.first()!!
            history?.postValue(it.first!!)
        }
    }
}