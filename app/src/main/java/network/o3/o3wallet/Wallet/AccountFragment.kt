package network.o3.o3wallet.Wallet

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.design.widget.FloatingActionButton
import android.widget.*
import android.os.Handler
import network.o3.o3wallet.API.CoZ.Claims
import network.o3.o3wallet.API.CoZ.CoZClient
import android.support.v4.widget.SwipeRefreshLayout
import android.content.Intent
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.android.synthetic.main.fragment_account.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.*
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.onUiThread

interface TokenListProtocol {
    fun reloadTokenList()
}

class AccountFragment : Fragment(), TokenListProtocol {

    private var fabExpanded = false
    private lateinit var menuButton: FloatingActionButton
    private lateinit var unclaimedGASLabel: TickerView
    private lateinit var claimButton: Button
    private lateinit var claims: Claims
    private lateinit var currentAccountState: AccountState
    private lateinit var neoBalance: Balance
    private lateinit var gasBalance: Balance
    private lateinit var qrButton: ImageButton
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var assetListView: ListView
    private lateinit var claimToast: Toast

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_account, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        claimProgress.visibility = View.INVISIBLE

        menuButton = view.findViewById<FloatingActionButton>(R.id.menuActionButton)
        claimButton = view.findViewById<Button>(R.id.claimButton)
        qrButton = view.findViewById<ImageButton>(R.id.qrButton)
        unclaimedGASLabel = view.findViewById(R.id.unclaimedGASLabel)
        assetListView = view.findViewById<ListView>(R.id.assetListView)
        unclaimedGASLabel.setCharacterList(TickerUtils.getDefaultNumberList());

        val muli = ResourcesCompat.getFont(view.context, R.font.muli_bold)

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

        activity?.title = "Account"

        //Account state doesn't return balance array if account has zero NEO and GAS
        //we must init balance with 0
        neoBalance = Balance( NeoNodeRPC.Asset.NEO.assetID(),0.0)
        gasBalance= Balance( NeoNodeRPC.Asset.GAS.assetID(),0.0)

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

    override fun reloadTokenList() {
        loadAccountState()
    }

    public fun addNewNEP5Token() {
        val bottomSheet = NEP5ListFragment()
        bottomSheet.delegate = this
        bottomSheet.show(activity!!.supportFragmentManager, "nep5list")
    }

    private fun showAccountState(data: Pair<AccountState?, Error?>): Unit {

        val error = data.second
        val accountState = data.first

        if (error != null) {
            swipeContainer.isRefreshing = false
            context!!.toast(error.message!!)
            return
        }

        this.currentAccountState = accountState!!
        swipeContainer.isRefreshing = false
        //construct array of AccountAsset
        var assets: ArrayList<AccountAsset> = arrayListOf<AccountAsset>()

        for (balance in accountState!!.balances.iterator()) {
            //NEO
            if (balance.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                this.neoBalance = balance
                this.enableClaimGASButton(balance.value)

            } else if (balance.asset.contains(NeoNodeRPC.Asset.GAS.assetID())) {
                this.gasBalance = balance
            }
        }

        var neo = AccountAsset(assetID = NeoNodeRPC.Asset.NEO.assetID(),
                name = NeoNodeRPC.Asset.NEO.name,
                symbol = NeoNodeRPC.Asset.NEO.name,
                decimal = 0,
                type = AssetType.NATIVE,
                value = neoBalance.value)
        assets.add(neo)

        var gas = AccountAsset(assetID = NeoNodeRPC.Asset.GAS.assetID(),
                name = NeoNodeRPC.Asset.GAS.name,
                symbol = NeoNodeRPC.Asset.GAS.name,
                decimal = 0,
                type = AssetType.NATIVE,
                value = gasBalance.value)
        assets.add(gas)

        val selectedToken = PersistentStore.getSelectedNEP5Tokens()

        selectedToken.all { t ->
            var token = t.value
            var asset =  AccountAsset(assetID = token.assetID,
                    name = token.name,
                    symbol = token.symbol,
                    decimal = token.decimal,
                    type = AssetType.NEP5TOKEN,
                    value = 0.0)
            assets.add(asset)
        }

        val adapter = AccountAssetsAdapter(this,context,Account.getWallet()!!.address, assets.toTypedArray())
        assetListView.adapter = adapter
    }

    private fun loadAccountState() {
        claimButton.isEnabled = false
        async(UI) {
            bg {
                NeoNodeRPC(PersistentStore.getNodeURL()).getAccountState(address = Account.getWallet()!!.address) {
                    onUiThread {
                        showAccountState(it)
                    }
                }
            }
        }
    }

    private fun disableGasInfo() {
        if (waitingForClaimTextView != null) {
            claimButton.isEnabled = false
            unclaimedGASLabel.visibility = View.GONE
            waitingForClaimTextView.visibility = View.VISIBLE
        }
    }

    private fun enableGasInfo() {
        if (waitingForClaimTextView != null) {
            claimButton.isEnabled = true
            unclaimedGASLabel.visibility = View.VISIBLE
            waitingForClaimTextView.visibility = View.GONE
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
                activity?.runOnUiThread {
                    unclaimedGASLabel.text = "%.8f".format(amount)
                    this.claimButton.isEnabled = if (amount == 0.0 || unclaimedGASLabel.visibility == View.GONE) false else true
                }
            }
        }
    }

    private fun enableClaimGASButton(neoAmount: Double) {
        claimButton.isEnabled = if (neoAmount == 0.0  || unclaimedGASLabel.visibility == View.GONE) false else true
        if (claimButton.isEnabled == true) {
            loadClaimableGasEvery5Seconds()
        }
    }

    fun performClaim() {
        CoZClient().getClaims(Account.getWallet()!!.address) {
            val claims = it.first
            val error = it.second
            if (error != null) {
                onUiThread {
                    context?.toast(error!!.message!!)
                    claimButton.isEnabled = true
                    claimProgress.visibility = View.INVISIBLE
                    claimToast.cancel()
                }
            } else if (claims?.claims?.size == 0) {
                Handler().postDelayed({ performClaim() }, 5000)
            } else {
                NeoNodeRPC(PersistentStore.getNodeURL()).claimGAS(Account.getWallet()!!) {
                    onUiThread {
                        var success = it.first
                        var error = it.second
                        if (success == true) {
                            claimToast.cancel()
                            claimButton.isEnabled = false
                            claimProgress.visibility = View.INVISIBLE
                            context!!.toast(resources.getString(R.string.claimed_gas_successfully))
                            disableGasInfo()
                            Handler().postDelayed({ enableGasInfo() }, 180000)
                            loadAccountState()
                            loadClaimableGAS()
                        }
                    }
                }
            }
        }
    }

    fun claimGasTapped() {
        claimToast = context!!.toastUntilCancel(resources.getString(R.string.claiming_gas))
        if (this.currentAccountState == null) {
            return
        }
        claimProgress.visibility = View.VISIBLE
        claimButton.isEnabled = false
        claimToast.show()

        NeoNodeRPC(PersistentStore.getNodeURL()).sendAssetTransaction(Account.getWallet()!!, NeoNodeRPC.Asset.NEO, neoBalance.value, Account.getWallet()!!.address, null) {
            var error = it.second
            var success = it.first
            if (error != null) {
                onUiThread {
                    claimButton.isEnabled = true
                    claimToast.cancel()
                    claimProgress.visibility = View.INVISIBLE
                    context?.toast(error!!.message!!)
                }
            } else if (success == true) {
                performClaim()
            }
        }
    }

    private fun menuButtonTapped() {
        val intent: Intent = Intent(
                context,
                SendActivity::class.java
        )
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this.activity!!, menuButton, ViewCompat.getTransitionName(menuButton))
        ActivityCompat.startActivity(context!!, intent, option.toBundle())
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}