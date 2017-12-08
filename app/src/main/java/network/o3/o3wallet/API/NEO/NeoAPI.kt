package network.o3.o3wallet.API.NEO

import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import neowallet.Wallet
import network.o3.o3wallet.API.CoZ.*
import network.o3.o3wallet.hexStringToByteArray
import network.o3.o3wallet.toHex
import neowallet.Neowallet
import java.math.BigInteger
import android.R.array
import android.util.Size
import java.nio.*


class NeoNodeRPC {
    var nodeURL = "http://seed3.neo.org:10332"

    //    var nodeURL = "http://seed3.neo.org:20332" //TESTNET
    enum class Asset() {
        NEO,
        GAS;

        fun assetID(): String {
            if (this == GAS) {
                return "602c79718b16e442de58778e148d0b1084e3b2dffd5de6b7b16cee7969282de7"
            } else if (this == NEO) {
                return "c56f33fc6ecfcd0c225c4ab356fee59390af8560be0e930faebe74a6daff7c9b"
            }
            return ""
        }
    }

    enum class RPC() {
        GETBLOCKCOUNT,
        GETCONNECTIONCOUNT,
        VALIDATEADDRESS,
        GETACCOUNTSTATE,
        SENDRAWTRANSACTION;

        fun methodName(): String {
            return this.name.toLowerCase()
        }
    }

    fun getBlockCount(completion: (Pair<Int?, Error?>) -> (Unit)) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETBLOCKCOUNT.methodName(),
                "params" to jsonArray(),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        println(RPC.GETBLOCKCOUNT.methodName())
        request.headers["Content-Type"] = "application/json"
        request.responseString { request, response, result ->
            print(result.component1())

            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                val blockCount = gson.fromJson<Int>(nodeResponse.result)
                completion(Pair<Int?, Error?>(blockCount, null))
            } else {
                completion(Pair<Int?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun getConnectionCount(completion: (Pair<Int?, Error?>) -> Unit) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETCONNECTIONCOUNT.methodName(),
                "params" to jsonArray(),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        request.headers["Content-Type"] = "application/json"
        request.responseString { request, response, result ->

            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                val blockCount = gson.fromJson<Int>(nodeResponse.result)
                completion(Pair<Int?, Error?>(blockCount, null))
            } else {
                completion(Pair<Int?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun getAccountState(address: String, completion: (Pair<AccountState?, Error?>) -> Unit) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETACCOUNTSTATE.methodName(),
                "params" to jsonArray(address),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        request.headers["Content-Type"] = "application/json"
        request.responseString { request, response, result ->

            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                val block = gson.fromJson<AccountState>(nodeResponse.result)
                completion(Pair<AccountState?, Error?>(block, null))
            } else {
                completion(Pair<AccountState?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun validateAddress(address: String, completion: (Pair<Boolean?, Error?>) -> Unit) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETACCOUNTSTATE.methodName(),
                "params" to jsonArray(address),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        request.headers["Content-Type"] = "application/json"
        request.responseString { request, response, result ->
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                val validatedAddress = gson.fromJson<ValidatedAddress>(nodeResponse.result)
                completion(Pair<Boolean?, Error?>(validatedAddress.isValid, null))
            } else {
                completion(Pair<Boolean?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun sendRawTransaction(data: ByteArray, completion: (Pair<Boolean?, Error?>) -> Unit) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.SENDRAWTRANSACTION.methodName(),
                "params" to jsonArray(data.toHex()),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        request.headers["Content-Type"] = "application/json"
        request.responseString { request, response, result ->
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                val success = gson.fromJson<Boolean>(nodeResponse.result["result"])
                completion(Pair<Boolean?, Error?>(success, null))
            } else {
                completion(Pair<Boolean?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    /*
    fun getBlockBy(index: Int, completion: (Pair<Block?, Error?>) -> (Unit)) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETBLOCK.methodName(),
                "params" to jsonArray(index, 1),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        println(RPC.GETBLOCKCOUNT.methodName())
        request.httpHeaders["Content-Type"] =  "application/json"
        request.responseString { request, response, result ->
        println(request)
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                println(data)
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                println (nodeResponse.toString())
                val block = gson.fromJson<Block>(nodeResponse.result)
                completion(Pair<Block?, Error?>(block, null))
            } else {
                completion(Pair<Block?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }



    fun getBlockBy(hash: String, completion: (Pair<Block?, Error?>) -> (Unit)) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETBLOCK.methodName(),
                "params" to jsonArray(hash, 1),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        println(RPC.GETBLOCKCOUNT.methodName())
        request.httpHeaders["Content-Type"] =  "application/json"
        request.responseString { request, response, result ->
            println(request)
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                println(data)
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                println (nodeResponse.toString())
                val block = gson.fromJson<Block>(nodeResponse.result)
                completion(Pair<Block?, Error?>(block, null))
            } else {
                completion(Pair<Block?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun getTransactionBy(hash: String, completion: (Pair<Transaction?, Error?>) -> (Unit)) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.GETRAWTRANSACTION.methodName(),
                "params" to jsonArray(hash, 1),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        println(RPC.GETBLOCKCOUNT.methodName())
        request.httpHeaders["Content-Type"] =  "application/json"
        request.responseString { request, response, result ->
            println(request)
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                println(data)
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                println (nodeResponse.toString())
                val transaction = gson.fromJson<Transaction>(nodeResponse.result)
                completion(Pair<Transaction?, Error?>(transaction, null))
            } else {
                completion(Pair<Transaction?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }*/

    fun claimGAS(wallet: Wallet, completion: (Pair<Boolean?, Error?>) -> (Unit)) {
        CoZClient().getClaims(wallet.address) {
            val claims = it.first
            var error = it.second
            if (error != null) {
                completion(Pair<Boolean?, Error?>(false, error))
            } else {
                val payload = generateClaimTransactionPayload(wallet, claims!!)
                System.out.println(payload.toHex())
                sendRawTransaction(payload) {
                    var success = it.first
                    var error = it.second
                    completion(Pair<Boolean?, Error?>(success, error))
                }

            }
        }
    }

    private fun concatenatePayloadData(wallet: Wallet, txData: ByteArray, signatureData: ByteArray): ByteArray {
        var payload = txData + byteArrayOf(0x01.toByte())                        // signature number
        payload += byteArrayOf(0x41.toByte())                              // signature struct length
        payload += byteArrayOf(0x40.toByte())                                 // signature data length
        payload += signatureData                   // signature
        payload += byteArrayOf(0x23.toByte())                                 // contract data length
        payload = payload + byteArrayOf(0x21.toByte()) + wallet.publicKey + byteArrayOf(0xac.toByte()) // NeoSigned publicKey
        return payload
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        var i = 0
        while (i < len) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
            i += 2
        }
        return data
    }

    private fun generateClaimInputData(wallet: Wallet, claims: Claims): ByteArray {
        var payload: ByteArray = byteArrayOf(0x02.toByte()) // Claim Transaction Type
        payload += byteArrayOf(0x00.toByte()) // Version
        val claimsCount = claims.claims.count().toByte()
        payload += byteArrayOf(claimsCount)
        for (claim: Claim in claims.claims) {
            payload += hexStringToByteArray(claim.txid).reversedArray()
            payload += ByteBuffer.allocate(2).order(ByteOrder.LITTLE_ENDIAN).putShort(claim.index.toShort()).array()
        }
        payload += byteArrayOf(0x00.toByte()) // Attributes
        payload += byteArrayOf(0x00.toByte()) // Inputs
        payload += byteArrayOf(0x01.toByte()) // Output Count
        payload += hexStringToByteArray(NeoNodeRPC.Asset.GAS.assetID()).reversedArray()
        payload += ByteBuffer.allocate(8).order(ByteOrder.LITTLE_ENDIAN).putInt(claims.total_claim).array()
        payload += wallet.hashedSignature

        return payload
    }

    private fun generateClaimTransactionPayload(wallet: Wallet, claims: Claims): ByteArray {
        val rawClaim = generateClaimInputData(wallet, claims)
        val privateKeyHex = wallet.privateKey.toHex()
        val signature = Neowallet.sign(rawClaim, privateKeyHex)
        val finalPayload = concatenatePayloadData(wallet, rawClaim, signature)
        return finalPayload
    }

}