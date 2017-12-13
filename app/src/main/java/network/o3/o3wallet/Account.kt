package network.o3.o3wallet

import neowallet.Neowallet
import neowallet.Wallet
import network.o3.o3wallet.Crypto.Decryptor
import network.o3.o3wallet.Crypto.Encryptor
import network.o3.o3wallet.Crypto.EncryptedSettingsRepository
import network.o3.o3wallet.Crypto.EncryptedSettingsRepository.setProperty
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
        setProperty(alias, encryptedWIF.toHex(), iv, O3Wallet.appContext!!)
    }

    fun restoreWalletFromDevice() {
        val alias = "O3 Key"
        val storedVal = EncryptedSettingsRepository.getProperty(alias, O3Wallet.appContext!!)
        val storedEncryptedWIF = storedVal?.data?.hexStringToByteArray()!!
        val storedIv = storedVal?.iv!!
        val decrypted = Decryptor().decrypt(alias, storedEncryptedWIF, storedIv)
        wallet = Neowallet.generateFromWIF(decrypted)
    }

    fun createNewWallet() {
        val random = SecureRandom()
        var bytes = ByteArray(32)
        random.nextBytes(bytes)
        val hex = bytes.toHex()
        wallet = Neowallet.generatePublicKeyFromPrivateKey(hex)
        storeEncryptedKeyOnDevice()
    }

    fun deleteKeyFromDevice() {
        val alias = "O3 Key"
        setProperty(alias, "", kotlin.ByteArray(0), O3Wallet.appContext!!)
    }

    fun getWallet(): Wallet? {
        return wallet
    }
}
