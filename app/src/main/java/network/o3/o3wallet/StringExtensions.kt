package network.o3.o3wallet

import java.text.DateFormat
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by drei on 12/7/17.
 */
fun Double.formattedBTCString() : String {
    return "%.8f".format(this) + "BTC"
}

fun Double.formattedUSDString() : String {
    val formatter = NumberFormat.getCurrencyInstance()
    formatter.currency = Currency.getInstance("USD")
    return formatter.format(this)
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

fun Date.IntervaledString(interval: String): String {
    var dateFormat = ""
    if (interval == "6H" || interval == "24H") {
        dateFormat = "hh:mm"
    } else {
        dateFormat = "MMM d, hh:mm"
    }
    val dateFormatter = SimpleDateFormat(dateFormat)
    return "since " + dateFormatter.format(this)
}