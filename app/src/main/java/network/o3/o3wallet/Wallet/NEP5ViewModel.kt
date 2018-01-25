package network.o3.o3wallet.Wallet

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import network.o3.o3wallet.API.NEO.NEP5Token
import network.o3.o3wallet.API.NEO.NeoNetwork
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.NEO.Node
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.R

/**
 * Created by drei on 1/25/18.
 */

class NEP5ViewModel: ViewModel() {
    private var tokens: MutableLiveData<Array<NEP5Token>>? = null

    fun getNodesFromModel(refresh: Boolean): LiveData<Array<NEP5Token>> {
        if (tokens == null || refresh) {
            tokens = MutableLiveData()
            loadTokens()
        }
        return tokens!!
    }

    fun loadTokens() {
        O3API().getAvailableNEP5Tokens {
            if (it.second != null) {
                return@getAvailableNEP5Tokens
            } else {
                tokens!!.postValue(it.first!!)
            }
        }
    }
}