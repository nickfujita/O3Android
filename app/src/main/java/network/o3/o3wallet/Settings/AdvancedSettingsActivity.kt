package network.o3.o3wallet.Settings

import android.content.Context
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import network.o3.o3wallet.BuildConfig
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.alert
import org.jetbrains.anko.defaultSharedPreferences

class AdvancedSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_advanced_activity)
        val editText = findViewById<EditText>(R.id.customEndpointEditText)
        val setCustomButton = findViewById<Button>(R.id.connectButton)

        val mainnet = findViewById<CheckBox>(R.id.checkBoxMainNet)
        val testnet = findViewById<CheckBox>(R.id.checkBoxTestNet)
        val privatenet = findViewById<CheckBox>(R.id.checkBoxPrivateNet)

        privatenet.isChecked = true

        privatenet.setOnClickListener {
            testnet.isChecked = false
            mainnet.isChecked = false
            privatenet.isChecked = true
            editText.text = SpannableStringBuilder("https://privatenet.o3.network:30333")
        }

        testnet.setOnClickListener {
            testnet.isChecked = true
            mainnet.isChecked = false
            privatenet.isChecked = false
            editText.text = SpannableStringBuilder("http://seed2.neo.org:20332")
        }

        mainnet.setOnClickListener {
            testnet.isChecked = false
            mainnet.isChecked = true
            privatenet.isChecked = false
            editText.text = SpannableStringBuilder("http://seed2.o3node.org:10332")
        }

        setCustomButton.setOnClickListener {
            PersistentStore.setNodeURL(editText.text.toString())
            if (testnet.isChecked) {
                PersistentStore.setNetworkType("Test")
            } else if (mainnet.isChecked) {
                PersistentStore.setNetworkType("Main")
            } else {
                PersistentStore.setNetworkType("Private")
            }
        }
    }
}
