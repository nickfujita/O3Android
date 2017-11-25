package network.o3.o3wallet

import org.junit.Test

import org.junit.Assert.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import NeoNodeRPC
import network.o3.o3wallet.API.CoZ.CoZClient
import network.o3.o3wallet.API.O3.O3API

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class APIUnitTests {
    val testAddress = "AHa8Fk7Zyu2Vq3jYSuiHyCiNaibDiMsUMK"

    @Test
    fun getBlockCount() {
        var latch = CountDownLatch(1)

        NeoNodeRPC().getBlockCount {
            assert(it.first != null)
            assert(it.first!! > 0)
            print(it.first!!)
            latch.countDown()
        }
        latch.await(2000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun getConnectionCount() {
        var latch = CountDownLatch(1)

        NeoNodeRPC().getConnectionCount {
            assert(it.first != null)
            assert(it.first!! > 0)
            print(it.first!!)
            latch.countDown()
        }
        latch.await(2000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun getAccountState() {
        var latch = CountDownLatch(1)
        NeoNodeRPC().getAccountState(testAddress) {
            assert(it.first != null)
            print (it.first!!.toString())
            assert(it.first!!.balances[0].value > 0)
            latch.countDown()
        }
        latch.await(2000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun validateAddress() {
        var latch = CountDownLatch(2)
        NeoNodeRPC().validateAddress(testAddress) {
            assert(it.first != null)
            assert(it.first!! == true)
            print("hello")
            latch.countDown()
        }

        NeoNodeRPC().validateAddress("dsnfjsanfjd") {
            assert(it.first != null)
            assert(it.first!! == false)
            latch.countDown()
        }
        latch.await(3000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun getTransactionHistory() {
        var latch = CountDownLatch(1)
        CoZClient().getTransactionHistory(testAddress) {
            assert(it.first != null)
            print (it.first!!.toString())
            latch.countDown()
        }
        latch.await(2000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun getPortfolio() {
        var latch = CountDownLatch(1)
        O3API().getPortfolio(2, 2.0, 15) {
            assert(it.first != null)
            print (it.first!!.toString())
            latch.countDown()
        }
        latch.await(20000, TimeUnit.MILLISECONDS)
    }

    @Test
    fun getPriceHistory() {
        var latch = CountDownLatch(1)
        O3API().getPriceHistory("NEO", 15) {
            assert(it.first != null)
            print (it.first!!.toString())
            latch.countDown()
        }
        latch.await(20000, TimeUnit.MILLISECONDS)
    }
}
