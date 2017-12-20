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
import network.o3.o3wallet.API.CoZ.Claims
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.CoZ.CoZClient
import network.o3.o3wallet.API.NEO.AccountState
import network.o3.o3wallet.API.NEO.Balance
import network.o3.o3wallet.ui.toast
import network.o3.o3wallet.ui.toastUntilCancel
import android.support.v4.widget.SwipeRefreshLayout
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.opengl.ETC1.getHeight
import android.opengl.ETC1.getWidth
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.android.synthetic.main.fragment_settings.*
import android.support.design.widget.BottomSheetBehavior
import network.o3.o3wallet.*


class AccountFragment : Fragment() {

    private var fabExpanded = false
    private lateinit var menuButton: FloatingActionButton
    private lateinit var neoAmountLabel: TextView
    private lateinit var gasAmountLabel: TextView
    private lateinit var unclaimedGASLabel: TickerView
    private lateinit var claimButton: Button
    private lateinit var claims: Claims
    private lateinit var currentAccountState: AccountState
    private lateinit var neoBalance: Balance
    private lateinit var gasBalance: Balance
    private lateinit var qrButton: ImageButton
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

        claimButton = view!!.findViewById<Button>(R.id.claimButton)
        qrButton = view!!.findViewById<ImageButton>(R.id.qrButton)

        unclaimedGASLabel = view!!.findViewById(R.id.unclaimedGASLabel)
        unclaimedGASLabel.setCharacterList(TickerUtils.getDefaultNumberList());
        val muli = ResourcesCompat.getFont(view!!.context, R.font.muli_bold)

        swipeContainer = view!!.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)

        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = true
            this.loadAccountState()
            this.loadClaimableGAS()
        }

        unclaimedGASLabel.typeface = muli
        unclaimedGASLabel.text = "0.00000000"
        menuButton.setOnClickListener { menuButtonTapped() }
        claimButton.setOnClickListener { claimGasTapped() }
        qrButton.setOnClickListener { showMyAddress() }

        menuButton.transitionName = "reveal"
        claimButton.isEnabled = false

        activity.title = "Account"
        loadAccountState()
        loadClaimableGAS()
    }

    private fun loadClaimableGasEvery5Seconds() {
        val handler = Handler()
        val delay = 5000 //milliseconds
        handler.postDelayed(object : Runnable {
            override fun run() {
                //do something
                loadClaimableGAS()
                handler.postDelayed(this, delay.toLong())
            }
        }, delay.toLong())
    }

    private fun showMyAddress() {
        val addressBottomSheet = MyAddressFragment()
        addressBottomSheet.show(activity!!.supportFragmentManager, "myaddress")
    }

    private fun loadAccountState() {
        claimButton.isEnabled = false
        NeoNodeRPC().getAccountState(address = Account.getWallet()!!.address) {
            val error = it.second
            val accountState = it.first
            if (error != null) {
                //manage error here
                activity.runOnUiThread {
                    swipeContainer.isRefreshing = false
                    context!!.toast(error.message!!)
                }
            } else {
                this.currentAccountState = accountState!!
                activity.runOnUiThread {
                    swipeContainer.isRefreshing = false
                    for (balance in accountState!!.balances.iterator()) {
                        //NEO
                        if (balance.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                            this.neoBalance = balance
                            neoAmountLabel.text = "%d".format(balance.value.toInt())
                            this.enableClaimGASButton(balance.value)
                        } else if (balance.asset.contains(NeoNodeRPC.Asset.GAS.assetID())) {
                            gasAmountLabel.text = "%.8f".format(balance.value)
                            this.gasBalance = balance
                        }
                    }

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
                    unclaimedGASLabel.text = "%.8f".format(amount)
                    this.claimButton.isEnabled = if (amount == 0.0) false else true
                }
            }
        }
    }

    private fun enableClaimGASButton(neoAmount: Double) {
        claimButton.isEnabled = if (neoAmount == 0.0) false else true
        if (claimButton.isEnabled == true) {
            loadClaimableGasEvery5Seconds()
        }
    }

    fun claimGasTapped() {
        if (this.currentAccountState == null) {
            //manage error here
            return
        }
        this.claimButton.isEnabled = false
        val toast = context!!.toastUntilCancel("Claiming GAS")
        toast.show()
        val wallet = Account.getWallet()!!
        CoZClient().getClaims(wallet.address) {
            val claims = it.first
            val error = it.second
            if (error != null) {
                activity.runOnUiThread {
                    context.toast(error!!.message!!)
                    claimButton.isEnabled = true
                }
            } else if (error == null && claims!!.claims.count() > 0) {
                //able to claim now
                activity.run {
                    NeoNodeRPC().claimGAS(wallet) {
                        activity.runOnUiThread {
                            var success = it.first
                            var error = it.second
                            if (success == true) {
                                toast.cancel()
                                context!!.toast("Claimed GAS successfully")
                                loadAccountState()
                                loadClaimableGAS()
                            }
                        }
                    }
                }
            } else if (error == null && claims!!.claims.count() == 0) {
                activity.run {
                    //claims array is empty. user needs to send all NEO to itself
                    NeoNodeRPC().sendAssetTransaction(wallet, NeoNodeRPC.Asset.NEO, neoBalance.value, wallet.address, null) {
                        var error = it.second
                        var success = it.first
                        if (error != null) {
                            activity.runOnUiThread {
                                context.toast(error!!.message!!)
                                claimButton.isEnabled = true
                            }
                        } else {
                            if (success == true) {
                                //try to fetch it again after about 5 seconds to avoid sending repeatedly
                                val handler = Handler()
                                val delay = 5000 //milliseconds
                                handler.postDelayed(object : Runnable {
                                    override fun run() {
                                        activity.runOnUiThread {
                                            claimGasTapped()
                                        }
                                    }
                                }, delay.toLong())
                            } else {
                                activity.runOnUiThread {
                                    claimButton.isEnabled = true
                                }
                            }
                        }

                    }
                }
            }
        }
    }


    private fun menuButtonTapped() {
        val intent: Intent = Intent(
                context,
                SendActivity::class.java
        )
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this.activity, menuButton, ViewCompat.getTransitionName(menuButton))
        ActivityCompat.startActivity(context, intent, option.toBundle())
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}