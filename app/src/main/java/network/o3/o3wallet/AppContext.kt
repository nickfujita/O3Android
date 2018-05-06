package network.o3.o3wallet

import android.app.Application
import android.content.Context
import android.content.res.Configuration



/**
 * Created by drei on 11/29/17.
 */

class O3Wallet : Application() {
    override fun onCreate() {
        super.onCreate()
        O3Wallet.appContext = applicationContext
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}