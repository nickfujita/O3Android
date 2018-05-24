package network.o3.o3wallet.Topup

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.PopupMenu
import kotlinx.android.synthetic.main.topup_activity_topup_cold_storage_balance.*
import neoutils.Wallet
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.O3.O3API
import network.o3.o3wallet.Account
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R


//TODO: Eventually gonna have to make a ViewModel class for this
class TopupColdStorageBalanceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.topup_activity_topup_cold_storage_balance)
        initBalances()
        withdrawButton.setOnClickListener {
            val intent = Intent(this, TopupSendAmountActivity::class.java)
            startActivity(intent)
        }

        coldStorageMoreButton.setOnClickListener {
            val popup = PopupMenu(this, coldStorageMoreButton)
            popup.menuInflater.inflate(R.menu.cold_storage_menu, popup.menu)
            popup.setOnMenuItemClickListener {
                val itemId = it.itemId

                if (itemId == R.id.remove_cold_storage) {
                    Account.removeColdStorageKeyFragment()
                    PersistentStore.removeColdStorageVaultAddress()
                   // PersistentStore.setColdStorageEnabledStatus(false)
                    this.finish()
                }
                true
            }
            popup.show()
        }
    }

    fun initBalances() {
        val address = PersistentStore.getColdStorageVaultAddress()
        coldStorageAddressTextView.text = address
        NeoNodeRPC(PersistentStore.getNodeURL()).getAccountState(PersistentStore.getColdStorageVaultAddress()) {
            if (it.second != null) {
                return@getAccountState
            }
            for (balance in it.first?.balances!!) {
                runOnUiThread {
                    if (balance.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                        coldStorageNeoBalanceTextView.text = balance.value.toString()
                    } else {
                        coldStorageBalanceGasTextView.text = balance.value.toString()
                    }
                }
            }
        }
    }
}
