package network.o3.o3wallet.Settings

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import network.o3.o3wallet.BuildConfig
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.custom.onUiThread
import org.jetbrains.anko.defaultSharedPreferences

class AdvancedSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_advanced_activity)
        val editText = findViewById<EditText>(R.id.customEndpointEditText)
        val setCustomButton = findViewById<Button>(R.id.connectButton)

        setCustomButton.setOnClickListener {
            PersistentStore.setNodeURL(editText.text.toString())
            val sharedPref = O3Wallet.appContext!!.defaultSharedPreferences
            with (sharedPref.edit()) {
                putBoolean("USING_PRIVATE_NET", true)
                commit()
            }
        }
    }
}
