package network.o3.o3wallet

/**
 * Created by drei on 11/22/17.
 */
import java.security.MessageDigest
import network.o3.o3wallet.core.*

private val HEX_CHARS = "0123456789ABCDEF".toCharArray()

fun ByteArray.toHex() : String{
    val result = StringBuffer()

    forEach {
        val octet = it.toInt()
        val firstIndex = (octet and 0xF0).ushr(4)
        val secondIndex = octet and 0x0F
        result.append(HEX_CHARS[firstIndex])
        result.append(HEX_CHARS[secondIndex])
    }

    return result.toString()
}

fun String.hexStringToByteArray() : ByteArray {
    val len = this.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(this[i], 16) shl 4) + Character.digit(this[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun String.hashFromAddress(): String {
    val bytes = Base58.decodeChecked(this)
    val shortened = bytes.sliceArray(IntRange(0,20))
    return shortened.sliceArray(IntRange(1,shortened.count() - 1)).toHex()
}