import android.util.Log
import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.*
import com.google.gson.Gson
import com.google.gson.JsonObject

class NeoNodeRPC {
    var nodeURL = "http://seed2.neo.org:10332"

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
        request.headers["Content-Type"] =  "application/json"
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
        request.headers["Content-Type"] =  "application/json"
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
        request.headers["Content-Type"] =  "application/json"
        request.responseString { request, response, result ->

            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val nodeResponse = gson.fromJson<NodeResponse>(data!!)
                val block = gson.fromJson<AccountState>(nodeResponse.result)
                completion(Pair<AccountState?, Error?>(block, null))
            } else {
                Log.d("ERROR", error.localizedMessage)
                completion(Pair<AccountState?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun validateAddress(address: String, completion:(Pair<Boolean?, Error?>) -> Unit) {
        val dataJson = jsonObject(
                "jsonrpc" to "2.0",
                "method" to RPC.VALIDATEADDRESS.methodName(),
                "params" to jsonArray(address),
                "id" to 1
        )

        var request = nodeURL.httpPost().body(dataJson.toString())
        request.headers["Content-Type"] =  "application/json"
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
}