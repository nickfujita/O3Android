package network.o3.o3wallet.Portfolio

import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.API.O3.PriceData
import network.o3.o3wallet.API.NEO.*
import java.util.concurrent.CountDownLatch
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.util.Log
import network.o3.o3wallet.*
import network.o3.o3wallet.API.CoZ.NEO
import network.o3.o3wallet.API.O3.Portfolio


/**
 * Created by drei on 12/6/17.
 */

interface HomeViewModelProtocol {
    fun updateBalanceData(assets: ArrayList<AccountAsset>)
}

class HomeViewModel {
    enum class DisplayType(val position: Int) {
        HOT(0), COMBINED(1), COLD(2)
    }

    private var displayType: DisplayType = DisplayType.HOT
    private var interval: Int = 15
    private var currency = CurrencyType.USD
    private var portfolio: MutableLiveData<Portfolio>? = null
    private var balanceDispatchGroup =  DispatchGroup()

    lateinit var delegate: HomeViewModelProtocol


    var assetsReadOnly = ArrayList<AccountAsset>()
    var assetsWritable = ArrayList<AccountAsset>()
    var watchAddresses = PersistentStore.getWatchAddresses()
    var isLoadingData = false
    private var latestPrice: PriceData? = null
    private var initialPrice: PriceData? = null

    fun setCurrency(currency: CurrencyType) {
        this.currency = currency
    }

    fun getCurrency(): CurrencyType {
        return currency
    }

    fun setInterval(interval: Int) {
        this.interval = interval
    }

    fun getInterval(): Int {
        return this.interval
    }


    fun setDisplayType(displayType: DisplayType) {
        this.displayType = displayType
    }

    fun getDisplayType(): DisplayType {
        return this.displayType
    }

    fun addReadOnlyAsset(asset: AccountAsset) {
        val index = assetsReadOnly.indices?.find { assetsReadOnly[it].name == asset.name } ?: -1
        if (index == -1) {
            assetsReadOnly.add(asset)
        } else {
            assetsReadOnly[index].value += asset.value
        }
    }

    fun addWritableAsset(asset: AccountAsset) {
        assetsWritable.add(asset)
    }


    fun loadAssetsFromModel(useCached: Boolean) {
        if (!useCached) {
            assetsReadOnly.clear()
            assetsWritable.clear()
            loadAssetsForAllAddresses()
        } else when (displayType) {
            DisplayType.HOT -> delegate.updateBalanceData(assetsWritable)
            DisplayType.COLD -> delegate.updateBalanceData(assetsReadOnly)
            DisplayType.COMBINED -> delegate.updateBalanceData(assetsReadOnly)
        }
    }

    private fun loadAssetsForAllAddresses() {
        loadAssetsFor(Account.getWallet()?.address!!, false)
        for (address in watchAddresses) {
            loadAssetsFor(address.address, true)
        }
        val myRunnable = Runnable {
            delegate.updateBalanceData(assetsWritable)
        }
        balanceDispatchGroup.notify(myRunnable)
    }

    fun loadAssetsFor(address: String, isReadOnly: Boolean) {
        balanceDispatchGroup.enter()
        Log.d("LOADING ASSETS FOR: ", address)
        NeoNodeRPC(PersistentStore.getNodeURL()).getAccountState(address) {
            if (it.second != null) {
                balanceDispatchGroup.leave()
                return@getAccountState
            }
            for (asset in it.first?.balances!!) {
                var assetToAdd: AccountAsset
                if (asset.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                    assetToAdd = AccountAsset(assetID = NeoNodeRPC.Asset.NEO.assetID(),
                            name = NeoNodeRPC.Asset.NEO.name,
                            symbol = NeoNodeRPC.Asset.NEO.name,
                            decimal = 0,
                            type = AssetType.NATIVE,
                            value = asset.value)
                } else {
                    assetToAdd = AccountAsset(assetID = NeoNodeRPC.Asset.GAS.assetID(),
                            name = NeoNodeRPC.Asset.GAS.name,
                            symbol = NeoNodeRPC.Asset.GAS.name,
                            decimal = 0,
                            type = AssetType.NATIVE,
                            value = asset.value)
                }

                if (isReadOnly) {
                    this.addReadOnlyAsset(assetToAdd)
                } else {
                    this.addWritableAsset(assetToAdd)
                }
                balanceDispatchGroup.leave()
            }
        }

        for (key in PersistentStore.getSelectedNEP5Tokens().keys) {
            val token = PersistentStore.getSelectedNEP5Tokens()[key]!!
            balanceDispatchGroup.enter()
            NeoNodeRPC(PersistentStore.getNodeURL()).getTokenBalanceOf(token.tokenHash, address) {
                if (it.second != null) {
                    balanceDispatchGroup.leave()
                    return@getTokenBalanceOf
                }
                val amountDecimal: Double = (it.first!!.toDouble() / (Math.pow(10.0, token.decimal.toDouble())))
                val tokenToAdd = AccountAsset(assetID = token.tokenHash,
                        name = token.name,
                        symbol = token.symbol,
                        decimal = token.decimal,
                        type = AssetType.NEP5TOKEN,
                        value = amountDecimal)
                if (isReadOnly) {
                    this.addReadOnlyAsset(tokenToAdd)
                } else {
                    this.addWritableAsset(tokenToAdd)
                }
                balanceDispatchGroup.leave()
            }
        }
    }
}

    /*
     fun getAccountState(display: DisplayType? = null, refresh: Boolean): LiveData<Pair<Int, Double>> {
        if (neoGasColdStorage == null || neoGasHotWallet == null || refresh) {
            neoGasColdStorage = MutableLiveData()
            neoGasHotWallet = MutableLiveData()
            neoGasCombined = MutableLiveData()
            loadAccountState()
        }
        return if (display == null) {
             when (displayType) {
                DisplayType.HOT -> neoGasHotWallet!!
                DisplayType.COLD -> neoGasColdStorage!!
                DisplayType.COMBINED -> neoGasCombined!!
            }
        } else {
            return when (display!!) {
                DisplayType.HOT -> neoGasHotWallet!!
                DisplayType.COLD -> neoGasColdStorage!!
                DisplayType.COMBINED -> neoGasCombined!!
            }
        }
    }
     */

    /*fun getCurrentGasPrice(): Double {
        return if (currency == CurrencyType.USD) {
            portfolio?.value?.price?.get("gas")?.averageUSD ?: 0.0
        } else {
            portfolio?.value?.price?.get("gas")?.averageBTC ?: 0.0
        }
    }

    fun getFirstGasPrice(): Double {
        return if (currency == CurrencyType.USD) {
            portfolio?.value?.firstPrice?.get("gas")?.averageUSD ?: 0.0
        } else {
            portfolio?.value?.firstPrice?.get("gas")?.averageBTC ?: 0.0
        }
    }

    fun getCurrentNeoPrice(): Double {
        return if (currency == CurrencyType.USD) {
            portfolio?.value?.price?.get("neo")?.averageUSD ?: 0.0
        } else {
            portfolio?.value?.price?.get("neo")?.averageBTC?: 0.0
        }
    }

    fun getFirstNeoPrice(): Double {
        return if (currency == CurrencyType.USD) {
            portfolio?.value?.firstPrice?.get("neo")?.averageUSD ?: 0.0
        } else {
            portfolio?.value?.firstPrice?.get("neo")?.averageBTC ?: 0.0
        }
    }


    fun getInitialPortfolioValue(): Double  {
        return when(currency) {
            CurrencyType.BTC -> initialPrice?.averageBTC ?: 0.0
            CurrencyType.USD -> initialPrice?.averageUSD ?: 0.0
        }
    }

    fun getCurrentPortfolioValue(): Double {
        return when(currency) {
            CurrencyType.BTC -> latestPrice?.averageBTC ?: 0.0
            CurrencyType.USD -> latestPrice?.averageUSD ?: 0.0
        }
    }

    fun getPercentChange(): Double {
        if (getInitialPortfolioValue() == 0.0) return 0.0
        return ((getCurrentPortfolioValue() - getInitialPortfolioValue()) / getInitialPortfolioValue()* 100)
    }



    fun getPortfolioFromModel(refresh: Boolean): LiveData<Portfolio> {
        if (portfolio == null || refresh) {
            portfolio = MutableLiveData()
            loadPortfolio()
        }
        return portfolio!!
    }

    fun getPriceFloats(): FloatArray {
        val data: Array<Double>? = when (currency) {
            CurrencyType.USD -> portfolio?.value?.data?.map { it.averageUSD }?.toTypedArray()
            CurrencyType.BTC -> portfolio?.value?.data?.map { it.averageBTC }?.toTypedArray()
        }
        if (data == null) {
            return FloatArray(0)
        }

        var floats = FloatArray(data?.count())
        for (i in data!!.indices) {
            floats[i] = data[i].toFloat()
        }
        return floats.reversedArray()
    }

    private fun loadPortfolio() {
        val balance = when (displayType) {
            DisplayType.HOT -> neoGasHotWallet?.value ?: Pair(0, 0.0)
            DisplayType.COLD ->  neoGasColdStorage?.value ?: Pair(0, 0.0)
            DisplayType.COMBINED ->  neoGasCombined?.value ?: Pair(0, 0.0)
        }

        O3API().getPortfolio(balance.first, balance.second, interval) {
            if ( it?.second != null ) return@getPortfolio
            latestPrice = it?.first!!.data?.first()!!
            initialPrice = it?.first!!.data?.last()!!
            portfolio?.postValue(it?.first!!)
        }
    }

    private fun loadAccountState() {
        var watchAddresses = PersistentStore.getWatchAddresses()

        val latch = CountDownLatch(1 + watchAddresses.size)
        var runningGasHot = 0.0
        var runningNeoHot = 0
        var runningGasCold = 0.0
        var runningNeoCold = 0

        if (Account.getWallet() == null) {
            return
        }

        NeoNodeRPC(PersistentStore.getNodeURL()).getAccountState(Account.getWallet()?.address!!) {
            if (it.second != null) {
                latch.countDown()
                return@getAccountState
            }
            var balances = it?.first?.balances!!
            for (balance in balances) {
                if (balance.asset == Asset.NEO.id) {
                    runningNeoHot += balance.value.toInt()
                } else {
                    runningGasHot += balance.value.toDouble()
                }
            }
            latch.countDown()
        }

        for (address: WatchAddress in watchAddresses) {
            NeoNodeRPC(PersistentStore.getNodeURL()).getAccountState(address.address) {
                if (it.second != null) {
                    latch.countDown()
                    return@getAccountState
                }
                var balances = it?.first?.balances!!
                for (balance in balances) {
                    if (balance.asset == Asset.NEO.id) {
                        runningNeoCold += balance.value.toInt()
                    } else {
                        runningGasCold += balance.value.toDouble()
                    }
                }
                latch.countDown()
            }
        }
        latch.await()
        neoGasColdStorage?.value = Pair(runningNeoCold, runningGasCold)
        neoGasHotWallet?.value = Pair(runningNeoHot, runningGasHot)
        neoGasCombined?.value = Pair(runningNeoCold + runningNeoHot, runningGasCold + runningGasHot)
    }*/
