package network.o3.o3wallet.ui.Account

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.design.widget.FloatingActionButton
import android.widget.*
import android.content.Context
import android.os.Handler
import network.o3.o3wallet.Account
import network.o3.o3wallet.API.CoZ.Claims
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.CoZ.CoZClient
import network.o3.o3wallet.API.NEO.AccountState
import network.o3.o3wallet.API.NEO.Balance
import network.o3.o3wallet.ui.toast
import network.o3.o3wallet.ui.toastUntilCancel
import android.support.v4.widget.SwipeRefreshLayout
import network.o3.o3wallet.R
import network.o3.o3wallet.MainActivity
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import network.o3.o3wallet.SendActivity
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth




class AccountFragment : Fragment() {

    private var fabExpanded = false
    private lateinit var menuButton: FloatingActionButton
    private lateinit var neoAmountLabel: TextView
    private lateinit var gasAmountLabel: TextView
    private lateinit var transactionListView: ListView
    private lateinit var claimButton: Button
    private lateinit var claims: Claims
    private lateinit var currentAccountState: AccountState
    private lateinit var neoBalance: Balance
    private lateinit var gasBalance: Balance
    private lateinit var swipeContainer: SwipeRefreshLayout


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        menuButton = view!!.findViewById<FloatingActionButton>(R.id.menuActionButton)
        neoAmountLabel = view!!.findViewById<TextView>(R.id.neoAmountLabel)
        gasAmountLabel = view!!.findViewById<TextView>(R.id.gasAmountLabel)
        transactionListView = view!!.findViewById<ListView>(R.id.transactionListView)
        claimButton = view!!.findViewById<Button>(R.id.claimButton)
        swipeContainer = view!!.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)

        swipeContainer.setColorSchemeResources(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light)

        menuButton.setOnClickListener { menuButtonTapped() }
        claimButton.setOnClickListener { claimGasTapped() }

        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = true
            this.refreshData()
        }
        loadAccountState()
        loadClaimableGAS()
        loadTransactionHistory()
    }

    override fun onResume() {
        super.onResume()
        loadAccountState()
        loadTransactionHistory()
    }

    fun refreshData() {
        this.loadTransactionHistory()
        this.loadAccountState()
    }


    fun loadAccountState() {
        NeoNodeRPC().getAccountState(address = Account.getWallet()!!.address) {
            val error = it.second
            val accountState = it.first
            if (error != null) {
                //manage error here
                activity.runOnUiThread {
                    swipeContainer.isRefreshing = true
                }
            } else {
                this.currentAccountState = accountState!!
                activity.runOnUiThread {
                    for (balance in accountState!!.balances.iterator()) {
                        //NEO
                        if (balance.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                            this.neoBalance = balance
                            neoAmountLabel.text = "%d".format(balance.value.toInt())
                        } else if (balance.asset.contains(NeoNodeRPC.Asset.GAS.assetID())) {
                            gasAmountLabel.text = "%.8f".format(balance.value)
                            this.gasBalance = balance
                        }
                    }
                    swipeContainer.isRefreshing = false
                }
            }

        }
    }

    fun loadClaimableGAS() {
        CoZClient().getClaims(address = Account.getWallet()!!.address) {
            var error = it.second
            var data = it.first
            if (error != null) {

            } else {
                this.claims = data!!
                val amount = data!!.total_unspent_claim / 100000000.0
                activity.runOnUiThread {
                    claimButton.text = "Claim %.8f".format(amount)
                }
            }
        }
    }
    fun claimGasAsync() {

    }
    fun claimGasTapped() {
        if (this.currentAccountState == null) {
            //manage error here
            return
        }
        val toast = context!!.toastUntilCancel("Claiming GAS")
        toast.show()
        val wallet = Account.getWallet()!!
        CoZClient().getClaims(wallet.address) {
            val claims = it.first
            val error = it.second
            if (error != null) {
                //show error
            } else if (error == null && claims!!.claims.count() > 0) {
                //able to claim now
                NeoNodeRPC().claimGAS(wallet) {
                    activity.runOnUiThread {
                        var success = it.first
                        var error = it.second
                        if (success == true) {
                            toast.cancel()
                            context!!.toast("Claimed GAS successfully")
                            this.loadAccountState()
                        }
                    }
                }
            } else if (error == null && claims!!.claims.count() == 0) {
                //claims array is empty. user needs to send all NEO to itself
                NeoNodeRPC().sendAssetTransaction(wallet, NeoNodeRPC.Asset.NEO, this.neoBalance.value, wallet.address, null) {
                    var error = it.second
                    var success = it.first
                    if (success == true) {
                        //try to fetch it again after about 5 seconds to avoid sending repeatedly
                        Handler().postDelayed({
                            activity.runOnUiThread {
                                this.claimGasTapped()
                            }
                        }, 5000)

                    }
                }
            }
        }
    }

    fun loadTransactionHistory() {
        CoZClient().getTransactionHistory(address = Account.getWallet()!!.address) {
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
        val intent:Intent = Intent(context,SendActivity::class.java)
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this.activity,menuButton,"transition")
        ActivityCompat.startActivity(context,intent,option.toBundle())
    }



    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}