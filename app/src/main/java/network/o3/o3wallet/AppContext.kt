package network.o3.o3wallet

import android.app.Application
import android.content.Context
import android.content.res.Configuration
import com.akexorcist.localizationactivity.core.LocalizationApplicationDelegate



/**
 * Created by drei on 11/29/17.
 */

class O3Wallet : Application() {
    override fun onCreate() {
        super.onCreate()
        O3Wallet.appContext = applicationContext
    }

    var localizationDelegate = LocalizationApplicationDelegate(this)

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(localizationDelegate.attachBaseContext(base))
    }

    override  fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        localizationDelegate.onConfigurationChanged(this)
    }

    override fun getApplicationContext(): Context {
        return localizationDelegate.getApplicationContext(super.getApplicationContext())
    }

    companion object {
        var appContext: Context? = null
            private set
    }
}