package network.o3.o3wallet.Portfolio

import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.API.O3.PriceData
import network.o3.o3wallet.API.NEO.*
import android.util.Log
import network.o3.o3wallet.*
import network.o3.o3wallet.API.O3.Portfolio
import org.jetbrains.anko.coroutines.experimental.bg
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.util.*
import java.util.concurrent.CountDownLatch
import kotlin.collections.ArrayList


/**
 * Created by drei on 12/6/17.
 */

interface HomeViewModelProtocol {
    fun updateBalanceData(assets: ArrayList<AccountAsset>)
    fun updatePortfolioData(portfolio: Portfolio)
    fun showLoadingIndicator()
    fun hideLoadingIndicator()
}

class HomeViewModel {
    enum class DisplayType(val position: Int) {
        HOT(0), COMBINED(1), COLD(2)
    }

    private var displayType: DisplayType = DisplayType.HOT
    private var interval: String = "24H"
    private var currency = CurrencyType.USD
    lateinit private var portfolio: Portfolio
    private var balanceCountDownLatch: CountDownLatch? = null

    lateinit var delegate: HomeViewModelProtocol


    var assetsReadOnly = arrayListOf<AccountAsset>()
    var assetsWritable = arrayListOf<AccountAsset>()
    var watchAddresses = PersistentStore.getWatchAddresses()
    var tokens = PersistentStore.getSelectedNEP5Tokens()
    var isLoadingData = false
    private var latestPrice: PriceData? = null
    private var initialPrice: PriceData? = null

    fun setCurrency(currency: CurrencyType) {
        this.currency = currency
    }

    fun getCurrency(): CurrencyType {
        return currency
    }

    fun setInterval(interval: String) {
        this.interval = interval
    }

    fun getInterval(): String {
        return this.interval
    }

    fun deepCopyAssets(arrayList: ArrayList<AccountAsset>): ArrayList<AccountAsset> {
        val assetsDeepCopy = ArrayList<AccountAsset>()
        for (elem in arrayList) {
            if (elem.assetID != null) {
                assetsDeepCopy.add(AccountAsset(assetID = elem.assetID,
                        name = elem.name,
                        symbol = elem.symbol,
                        decimal = elem.decimal,
                        type = elem.type,
                        value = elem.value))
            }
        }
        return assetsDeepCopy
    }

    fun deepCopyAsset(asset: AccountAsset): AccountAsset {
        return AccountAsset(assetID = asset.assetID,
                name = asset.name,
                symbol = asset.symbol,
                decimal = asset.decimal,
                type = asset.type,
                value = asset.value)
    }

    fun getInitialPortfolioValue(): Double  {
        return when(currency) {
            CurrencyType.BTC -> initialPrice?.averageBTC ?: 0.0
            CurrencyType.USD -> initialPrice?.averageUSD ?: 0.0
        }
    }

    fun getInitialDate(): Date {
        val df1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'")
        return try {
            df1.parse(initialPrice?.time ?: "")
        } catch (e: ParseException) {
            return Date()
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


    fun setDisplayType(displayType: DisplayType) {
        this.displayType = displayType
    }

    fun getDisplayType(): DisplayType {
        return this.displayType
    }

    fun addReadOnlyAsset(asset: AccountAsset) {
        val index = assetsReadOnly.indices.find { assetsReadOnly[it].name == asset.name } ?: -1
        if (index == -1) {
            assetsReadOnly.add(asset)
        } else {
            assetsReadOnly[index].value += asset.value
        }
    }

    fun addWritableAsset(asset: AccountAsset) {
        assetsWritable.add(asset)
    }

    fun combineReadOnlyAndWritable(): ArrayList<AccountAsset>{
        var assets = deepCopyAssets(assetsWritable)
        for (asset in assetsReadOnly) {
            val index = assets.indices.find { assets[it].name == asset.name } ?: -1
            if (index == -1) {
                assets.add(deepCopyAsset(asset))
            } else {
                assets[index] = assets[index].copy()
                assets[index].value += asset.value
            }
        }
        return assets
    }

    fun getSortedAssets(): ArrayList<AccountAsset> {
        val assetsToSort = when (displayType) {
            DisplayType.HOT -> deepCopyAssets(assetsWritable)
            DisplayType.COMBINED -> deepCopyAssets(combineReadOnlyAndWritable())
            DisplayType.COLD -> deepCopyAssets(assetsReadOnly)
        }
        val sortedAssets = ArrayList<AccountAsset>()
        val neoIndex = assetsToSort.indices.find { assetsToSort[it].name == "NEO" } ?: -1
        //Make UTXO assets default supported
        if (neoIndex == -1) {
            sortedAssets.add(AccountAsset(assetID = NeoNodeRPC.Asset.NEO.assetID(),
                    name = NeoNodeRPC.Asset.NEO.name,
                    symbol = NeoNodeRPC.Asset.NEO.name,
                    decimal = 0,
                    type = AssetType.NATIVE,
                    value = 0.0))
        } else {
            sortedAssets.add(deepCopyAsset(assetsToSort[neoIndex]))
            assetsToSort.removeAt(neoIndex)
        }

        val gasIndex = assetsToSort.indices.find { assetsToSort[it].name == "GAS" } ?: -1
        if (gasIndex == -1) {
            sortedAssets.add(AccountAsset(assetID = NeoNodeRPC.Asset.GAS.assetID(),
                    name = NeoNodeRPC.Asset.GAS.name,
                    symbol = NeoNodeRPC.Asset.GAS.name,
                    decimal = 8,
                    type = AssetType.NATIVE,
                    value = 0.0))
        } else {
            sortedAssets.add(deepCopyAsset(assetsToSort[gasIndex]))
            assetsToSort.removeAt(gasIndex)
        }

        assetsToSort.sortBy { it.name }
        sortedAssets.addAll(deepCopyAssets(assetsToSort))

        return sortedAssets
    }

    //@Synchronized
    fun loadAssetsFromModel(useCached: Boolean) {
        if (balanceCountDownLatch != null && balanceCountDownLatch?.count?.toInt() != 0) {
            return
        }
        if (!useCached) {
            assetsReadOnly.clear()
            assetsWritable.clear()
            loadAssetsForAllAddresses()
        } else {
            delegate.updateBalanceData(getSortedAssets())
        }
    }

    private fun loadAssetsForAllAddresses() {
        balanceCountDownLatch = CountDownLatch((1 + tokens.size) * (watchAddresses.size + 1))
        loadAssetsFor(Account.getWallet()?.address!!, false)
        for (address in watchAddresses) {
            loadAssetsFor(address.address, true)
        }
        balanceCountDownLatch?.await()
        delegate.updateBalanceData(getSortedAssets())
    }

    fun loadAssetsFor(address: String, isReadOnly: Boolean) {
        bg {
            NeoNodeRPC(PersistentStore.getNodeURL()).getAccountState(address) {
                if (it.second != null) {
                    balanceCountDownLatch?.countDown()
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
                                decimal = 8,
                                type = AssetType.NATIVE,
                                value = asset.value)
                    }
                    if (isReadOnly) {
                        this.addReadOnlyAsset(assetToAdd)
                    } else {
                        this.addWritableAsset(assetToAdd)
                    }
                }
                balanceCountDownLatch?.countDown()
            }
        }

        for (key in PersistentStore.getSelectedNEP5Tokens().keys) {
            val token = PersistentStore.getSelectedNEP5Tokens()[key]!!
            bg {
                NeoNodeRPC(PersistentStore.getNodeURL()).getTokenBalanceOf(token.tokenHash, address) {
                    if (it.second != null) {
                        balanceCountDownLatch?.countDown()
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
                    balanceCountDownLatch?.countDown()
                }
            }
        }
    }

    fun getPriceFloats(): FloatArray {

        val data: Array<Double>? = when (currency) {
            CurrencyType.USD -> portfolio.data.map { it.averageUSD }?.toTypedArray()
            CurrencyType.BTC -> portfolio.data.map { it.averageBTC }?.toTypedArray()
        }
        if (data == null) {
            return FloatArray(0)
        }

        var floats = FloatArray(data.count())
        for (i in data.indices) {
            floats[i] = data[i].toFloat()
        }
        return floats.reversedArray()
    }

    fun loadPortfolioValue() {
        delegate.showLoadingIndicator()
        bg {
           O3API().getPortfolio(this.getSortedAssets(), this.interval) {
               if (it.second != null) {
                   return@getPortfolio
               }
               this.portfolio = it.first!!
               this.initialPrice = this.portfolio.data.last()
               this.latestPrice = this.portfolio.data.first()
               delegate.updatePortfolioData(it.first!!)
           }
        }
    }
}