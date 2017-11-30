package network.o3.o3wallet

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.design.widget.FloatingActionButton
import android.widget.*
import android.content.Context
import neowallet.Wallet
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.CoZ.CoZClient

class AccountFragment : Fragment() {

    private var fabExpanded = false
    private lateinit var menuButton: FloatingActionButton
    private lateinit var sendLayout: LinearLayout
    private lateinit var myAddressLayout: LinearLayout
    private lateinit var neoAmountLabel: TextView
    private lateinit var gasAmountLabel: TextView
    private lateinit var transactionListView: ListView

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuButton = view!!.findViewById<FloatingActionButton>(R.id.menuActionButton)
        sendLayout = view!!.findViewById<LinearLayout>(R.id.layoutFabSend)
        myAddressLayout = view!!.findViewById<LinearLayout>(R.id.layoutFabMyAddress)
        neoAmountLabel = view!!.findViewById<TextView>(R.id.neoAmountLabel)
        gasAmountLabel = view!!.findViewById<TextView>(R.id.gasAmountLabel)
        transactionListView = view!!.findViewById<ListView>(R.id.transactionListView)
        menuButton.setOnClickListener { menuButtonTapped() }
        closeMenu();
        loadAccountState()
        loadTransactionHistory()
    }

    override fun onResume() {
        super.onResume()
        loadAccountState()
        loadTransactionHistory()
    }

    fun loadAccountState() {
        NeoNodeRPC().getAccountState(address = Account.getWallet()!!.address) {
            val error = it.second
            val accountState = it.first
            if (error != null) {
                //manage error here
            } else {
                for (balance in accountState!!.balances.iterator()) {
                    //NEO
                    if (balance.asset == NeoNodeRPC.Asset.NEO.assetID()) {
                        neoAmountLabel.setText("%d".format(balance.value.toInt()))
                    } else if (balance.asset == NeoNodeRPC.Asset.GAS.assetID()) {
                        gasAmountLabel.setText("%.8f".format(balance.value))
                    }
                }
            }

        }
    }

    fun loadTransactionHistory() {
        CoZClient().getTransactionHistory(address = "") {
            var error = it.second
            var data = it.first
            if (error != null) {

            } else {
                activity.runOnUiThread(Runnable {
                    kotlin.run {
                        val adapter = TransactionHistoryAdapter(activity as Context, data!!.history)
                        transactionListView.adapter = adapter
                    }
                })
            }
        }
    }

    fun menuButtonTapped() {
        if (fabExpanded == true) {
            closeMenu();
        } else {
            openMenu();
        }
    }

    fun openMenu() {
        sendLayout.visibility = View.VISIBLE
        myAddressLayout.visibility = View.VISIBLE
        fabExpanded = true
    }

    fun closeMenu() {
        sendLayout.visibility = View.INVISIBLE
        myAddressLayout.visibility = View.INVISIBLE
        fabExpanded = false
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}