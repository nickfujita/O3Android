package network.o3.o3wallet

import android.util.Log
import com.robinhood.spark.SparkView
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.API.O3.PriceData
import NeoNodeRPC
import android.view.Display
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import android.content.Context
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData



/**
 * Created by drei on 12/6/17.
 */

class HomeViewModel: ViewModel()  {
    enum class DisplayType(val position: Int) {
        HOT(0), COMBINED(1), COLD(2)
    }

    enum class Currency {
        BTC, USD
    }

    enum class Asset(id: String) {
        NEO("0xc56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b"),
        GAS("0x602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7")
    }

    private var displayType: DisplayType = DisplayType.COMBINED
    private var interval: Int = 15
    private var currency: Currency = Currency.BTC
    private var neoGasColdStorage: MutableLiveData<Pair<Int, Double>>? = null
    private var neoGasHotWallet: MutableLiveData<Pair<Int, Double>>? = null
    private var neoGasCombined: MutableLiveData<Pair<Int, Double>>? = null
    private var latestPrice: PriceData? = null
    private var portfolioData: FloatArray? = null

    fun setCurrency(currency: Currency) {
        this.currency = currency
    }

    fun getCurrency(): Currency {
        return currency
    }

    fun getInterval(): Int{
        return interval
    }

    fun setInterval(interval: Int) {
        this.interval = interval
    }

    fun setDisplayType(displayType: DisplayType) {
        this.displayType = displayType
    }

    fun getDisplayType(): DisplayType {
        return this.displayType
    }

    fun getDisplayedBalance(): LiveData<Pair<Int, Double>> {
        if (neoGasColdStorage == null || neoGasHotWallet == null) {
            neoGasColdStorage = MutableLiveData<Pair<Int, Double>>()
            neoGasHotWallet = MutableLiveData<Pair<Int, Double>>()
            neoGasCombined = MutableLiveData<Pair<Int, Double>>()
            getAccountState()
        }
        when (displayType) {
            DisplayType.HOT -> return neoGasHotWallet!!
            DisplayType.COLD ->  return neoGasColdStorage!!
            DisplayType.COMBINED ->  return neoGasCombined!!
        }
    }

    fun getPortfolio() {
        O3API().getPortfolio(getDisplayedBalance().value!!.first!!, getDisplayedBalance().value!!.second!!, interval) {
            if ( it?.second != null ) {
                return@getPortfolio
            }

            var data: Array<Double>
            if (currency == Currency.USD) {
                data = it.first?.data?.map { it.averageUSD }?.toTypedArray()!!
            } else {
                data = it.first?.data?.map { it.averageBTC }?.toTypedArray()!!
            }
            var floats = FloatArray(data.count())
            this.latestPrice = it.first?.data?.first()!!
                for (i in data.indices) {
                    floats[i] = data[i].toFloat()
                }

            this.portfolioData = floats
        }
    }

    fun getAccountState() {
        var watchAddresses = PersistentStore.getWatchAddresses()

        val latch = CountDownLatch(1 + watchAddresses.size)
        var runningGasHot = 0.0
        var runningNeoHot = 0
        var runningGasCold = 0.0
        var runningNeoCold = 0
        NeoNodeRPC().getAccountState(Account.getWallet()?.address!!) rpc@{
            if (it.second != null) {
                latch.countDown()
                return@rpc
            }
            var balances = it?.first?.balances!!
            for (balance in balances) {
                if (balance.asset == HomeViewModel.Asset.NEO.name) {
                    runningNeoHot += balance.value.toInt()
                } else {
                    runningGasHot += balance.value
                }
            }
            latch.countDown()
        }

        for (address: WatchAddress in watchAddresses) {
            NeoNodeRPC().getAccountState(address.address) rpc@{
                if (it.second != null) {
                    latch.countDown()
                    return@rpc
                }
                var balances = it?.first?.balances!!
                for (balance in balances) {
                    if (balance.asset == HomeViewModel.Asset.NEO.name) {
                        runningNeoCold += balance.value.toInt()
                    } else {
                        runningGasCold += balance.value
                    }
                }
                latch.countDown()
            }
        }
        latch.await()
        neoGasColdStorage?.value = Pair(runningNeoCold, runningGasCold)
        neoGasHotWallet?.value = Pair(runningNeoHot, runningGasHot)
        neoGasCombined?.value = Pair(runningNeoCold + runningNeoHot, runningGasCold + runningGasHot)
    }
}
