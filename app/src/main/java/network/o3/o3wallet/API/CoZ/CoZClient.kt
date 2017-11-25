package network.o3.o3wallet.API.CoZ

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.github.salomonbrys.kotson.fromJson
import com.github.salomonbrys.kotson.jsonArray
import com.github.salomonbrys.kotson.jsonObject
import com.google.gson.Gson

/**
 * Created by drei on 11/24/17.
 */

class CoZClient {
    val baseAPIURL = "http://api.wallet.cityofzion.io/v2/address/"
    enum class Route() {
        HISTORY,
        CLAIMS;

        fun routeName(): String {
            return this.name.toLowerCase() + "/"
        }
    }

    fun getTransactionHistory(address: String, completion: (Pair<TransactionHistory?, Error?>) -> (Unit)) {
        val url = baseAPIURL + Route.HISTORY.routeName() + address
        var request = url.httpGet()
        request.responseString { request, response, result ->
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val history = gson.fromJson<TransactionHistory>(data!!)
                completion(Pair<TransactionHistory?, Error?>(history, null))
            } else {
                completion(Pair<TransactionHistory?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun getClaims(address: String, completion: (Pair<Claims?, Error?>) -> Unit) {
        val url = baseAPIURL + Route.CLAIMS.routeName() + address
        var request = url.httpGet()
        request.responseString { request, response, result ->
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val history = gson.fromJson<Claims>(data!!)
                completion(Pair<Claims?, Error?>(history, null))
            } else {
                completion(Pair<Claims?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }
}
