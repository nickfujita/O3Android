package network.o3.o3wallet.Settings

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import network.o3.o3wallet.API.O3.PriceHistory
import Node
import NeoNodeRPC
import NeoNetwork
import android.app.Application
import android.arch.lifecycle.LiveData
import com.github.salomonbrys.kotson.fromJson
import com.google.gson.Gson
import network.o3.o3wallet.API.O3.Portfolio
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.R
import java.util.concurrent.CountDownLatch

/**
 * Created by drei on 12/11/17.
 */

class NetworkViewModel: ViewModel() {
    enum class NetworkType {
        MAIN, TEST
    }

    private var type: NetworkType = NetworkType.MAIN
    private var nodes: MutableLiveData<Array<Node>>? = null

    fun getNodesFromModel(refresh: Boolean): LiveData<Array<Node>> {
        if (nodes == null || refresh) {
            nodes = MutableLiveData()
            loadNodes()
        }
        return nodes!!
    }

    fun loadNodes() {
        val jsonString = O3Wallet.appContext?.resources?.openRawResource(R.raw.nodes).bufferedReader().use { it.readText() }
        var neoNodes: Array<Node>? = null
        if (type == NetworkType.MAIN) {
            neoNodes = Gson().fromJson<NeoNetwork>(jsonString).main
        } else {
            neoNodes = Gson().fromJson<NeoNetwork>(jsonString).test
        }

        val latch = CountDownLatch(neoNodes.count() * 2)
        val newNodes = ArrayList<Node>()
        for (node in neoNodes!!) {
            NeoNodeRPC(node.url).getConnectionCount {
                val connectionCount = it.first ?: 0
                latch.countDown()
                NeoNodeRPC(node.url).getBlockCount {
                    val blockCount = it.first ?: 0
                    newNodes.add(Node(node.url, blockCount, connectionCount))
                    latch.countDown()
                }
            }
        }
        latch.await()
        nodes?.postValue(newNodes?.toTypedArray())
    }
}