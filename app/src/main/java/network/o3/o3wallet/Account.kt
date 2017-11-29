package network.o3.o3wallet

import android.content.Context
import android.content.ContextWrapper
import android.provider.Settings
import android.util.Log
import neowallet.Neowallet
import neowallet.Wallet
import network.o3.o3wallet.Crypto.Decryptor
import network.o3.o3wallet.Crypto.Encryptor
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.Crypto.SettingsRepository
import java.security.SecureRandom

/**
 * Created by drei on 11/22/17.
 */

object Account {
    private var wallet: Wallet? = null

    private fun storeEncryptedKeyOnDevice() {
        val wif = wallet!!.wif
        val encryptor = Encryptor()
        val alias = "O3 Key"
        val encryptedWIF = encryptor.encryptText(alias, wif)!!

        val iv = encryptor.getIv()!!
        SettingsRepository.setProperty(alias, encryptedWIF.toHex(), iv, O3Wallet.appContext!!)

        /* Codes To Fetch and Decrypt the WIF. To be stored in memory
            val storedVal = SettingsRepository.getProperty(alias, O3Wallet.appContext!!)
            val storedEncryptedWIF = storedVal?.data?.hexStringToByteArray()!!
            val storedIv = storedVal?.iv!!
            val decrypted = Decryptor().decrypt(alias, storedEncryptedWIF, storedIv)
        */

    }

    fun createNewWallet() {
        val random = SecureRandom()
        var bytes = ByteArray(32)
        random.nextBytes(bytes)
        val hex = bytes.toHex()
        wallet = Neowallet.generatePublicKeyFromPrivateKey(hex)
        storeEncryptedKeyOnDevice()

    }

    fun getWallet(): Wallet? {
        return wallet
    }
}
