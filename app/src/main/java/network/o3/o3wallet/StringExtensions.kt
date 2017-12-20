package network.o3.o3wallet

import java.text.NumberFormat
import java.util.*

/**
 * Created by drei on 12/7/17.
 */
fun Double.formattedBTCString() : String {
    return "%.8f".format(this) + "BTC"
}

fun Double.formattedUSDString() : String {
    val fomatter = NumberFormat.getCurrencyInstance()
    fomatter.currency = Currency.getInstance("USD")
    return fomatter.format(this)
}

fun Double.formattedPercentString(): String {
    return  "%.2f".format(this) + "%"
}

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

enum class CurrencyType {
    BTC, USD
}

fun Double.formattedCurrencyString(currency: CurrencyType): String {
    return when(currency) {
        CurrencyType.BTC -> this.formattedBTCString()
        CurrencyType.USD -> this.formattedUSDString()
    }
}