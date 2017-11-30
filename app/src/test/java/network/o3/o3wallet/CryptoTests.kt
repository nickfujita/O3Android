package network.o3.o3wallet.Crypto

import org.junit.Test
import java.security.KeyStore
import javax.crypto.Cipher

/**
 * Created by drei on 11/29/17.
 */

class CryptoTests {
    @Test
    fun testEncryption(){
        val encryptor = Encryptor()
        val alias = "test alias"
        val stringToEncrypt = "Andrei is Cool"
        val encryptedString = encryptor.encryptText(alias, stringToEncrypt)
        val iv = encryptor.getIv()
        print (encryptedString)
        Decryptor().decrypt(alias, encryptedString!!, encryptor.getIv()!!)
        // Somewhere later in the code, to decrypt
        //val info = EncryptedSettingsRepository.getProperty(alias, context)

        // decrypt them
        // info?.data
        // info?.iv
     }
}