package network.o3.o3wallet

/**
 * Created by drei on 11/22/17.
 */
import java.security.MessageDigest

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

//    val result = ByteArray(length / 2)
//
//    for (i in 0 until length step 2) {
//        val firstIndex = HEX_CHARS.indexOf(this[i]);
//        val secondIndex = HEX_CHARS.indexOf(this[i + 1]);
//
//        val octet = firstIndex.shl(4).or(secondIndex)
//        result.set(i.shr(1), octet.toByte())
//    }
//
//    return result
}

fun String.hashFromAddress(): String {
    val HEX_CHARS = "0123456789ABCDEF"
    val shortened = this.toByteArray().sliceArray(IntRange(0,20))
    val hashOne = MessageDigest
            .getInstance("SHA-256")
            .digest(shortened)

    val hashTwo = MessageDigest
            .getInstance("SHA-256")
            .digest(hashOne)

    System.out.println(hashTwo.size)
    val result = StringBuilder(hashOne.size * 2)

    hashTwo.forEach {
        val i = it.toInt()
        result.append(HEX_CHARS[i shr 4 and 0x0f])
        result.append(HEX_CHARS[i and 0x0f])
    }

    return result.toString()
}