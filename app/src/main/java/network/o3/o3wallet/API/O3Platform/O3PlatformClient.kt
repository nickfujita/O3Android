package network.o3.o3wallet.API.O3Platform

import com.github.kittinunf.fuel.httpGet
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson

/**
 * Created by drei on 11/24/17.
 */

class O3PlatformClient {
    val baseAPIURL = "https://platform.o3.network/api/v1/neo/"
    enum class Route {
        CLAIMABLEGAS,
        UTXO;

        fun routeName(): String {
            return this.name.toLowerCase()
        }
    }


    fun getClaimableGAS(address: String, completion: (Pair<ClaimData?, Error?>) -> Unit) {
        val url = baseAPIURL + address + "/" + Route.CLAIMABLEGAS.routeName()
        var request = url.httpGet()
        request.headers["User-Agent"] =  ""
        request.responseString { _, _, result ->
            val (data, error) = result

            if (error == null) {
                val gson = Gson()
                val platformResponse = gson.fromJson<PlatformResponse>(data!!)
                val claims = Gson().fromJson<ClaimData>(platformResponse.result)
                completion(Pair<ClaimData?, Error?>(claims, null))
            } else {
                completion(Pair<ClaimData?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

    fun getUTXOS(address: String, completion: (Pair<Assets?, Error?>) -> Unit) {
        val url = baseAPIURL + address + "/" + Route.UTXO.routeName()
        var request = url.httpGet()
        request.headers["User-Agent"] =  ""
        request.timeout(600000).responseString { _, _, result ->
            val (data, error) = result
            if (error == null) {
                val gson = Gson()
                val platformResponse = gson.fromJson<PlatformResponse>(data!!)
                val assets = Gson().fromJson<Assets>(platformResponse.result)
                completion(Pair<Assets?, Error?>(assets, null))
            } else {
                completion(Pair<Assets?, Error?>(null, Error(error.localizedMessage)))
            }
        }
    }

}
