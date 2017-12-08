package network.o3.o3wallet

/**
 * Created by drei on 12/7/17.
 */
fun Double.formattedBTCString() : String {
    return "%.8f".format(this) + "BTC"
}

fun Double.formattedUSDString() : String {
    return "%.2f".format(this) + "USD"
}

fun Double.formattedPercentString(): String {
    return  "%.2f".format(this) + "%"
}

fun Double.formattedGASString(): String {
    return "%.2f".format(this)
}