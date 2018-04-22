package network.o3.o3wallet.Settings

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import org.jetbrains.anko.alert

class AdvancedSettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_advanced_activity)
        val editText = findViewById<EditText>(R.id.customEndpointEditText)
        val setCustomButton = findViewById<Button>(R.id.connectButton)

        setCustomButton.setOnClickListener {
           PersistentStore.setNodeURL(editText.text.toString())
            alert { "You are now connected to  " + editText.text.toString() }.show()
        }
    }
}
