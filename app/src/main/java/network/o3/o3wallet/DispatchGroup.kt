package network.o3.o3wallet

/**
 * Created by drei on 2/12/18.
 */

class DispatchGroup {

    private var count = 0
    var runnable: Runnable? = null

    @Synchronized
    fun enter() {
        count++
    }

    @Synchronized
    fun leave() {
        count--
        notifyGroup()
    }

    fun notify(r: Runnable) {
        runnable = r
        notifyGroup()
    }

    private fun notifyGroup() {
        if (count <= 0 && runnable != null) {
            runnable!!.run()
        }
    }
}