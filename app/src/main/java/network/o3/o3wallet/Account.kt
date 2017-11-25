package network.o3.o3wallet

import android.provider.Settings
import android.util.Log
import neowallet.Neowallet
import neowallet.Wallet
import java.security.SecureRandom

/**
 * Created by drei on 11/22/17.
 */

object Account {
    private var wallet: Wallet? = null
    fun createNewWallet() {
        val random = SecureRandom()
        var bytes = ByteArray(32)
        random.nextBytes(bytes)
        val hex = bytes.toHex()
        wallet = Neowallet.generatePublicKeyFromPrivateKey(hex)
    }

    fun getWallet(): Wallet? {
        return wallet
    }
}
