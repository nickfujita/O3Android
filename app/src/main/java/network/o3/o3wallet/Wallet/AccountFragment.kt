package network.o3.o3wallet.Wallet

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.widget.*
import android.os.Handler
import network.o3.o3wallet.API.CoZ.Claims
import network.o3.o3wallet.API.CoZ.CoZClient
import android.support.v4.widget.SwipeRefreshLayout
import android.content.Intent
import android.content.res.Resources
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.content.res.ResourcesCompat
import android.support.v4.view.ViewCompat
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.robinhood.ticker.TickerUtils
import com.robinhood.ticker.TickerView
import kotlinx.android.synthetic.main.wallet_fragment_account.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.*
import network.o3.o3wallet.TokenSales.TokenSalesActivity
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.onUiThread
import network.o3.o3wallet.Topup.TopupColdStorageBalanceActivity
import network.o3.o3wallet.Topup.TopupTutorial
import network.o3.o3wallet.Wallet.Send.SendActivity


interface TokenListProtocol {
    fun reloadTokenList()
}

class AccountFragment : Fragment(), TokenListProtocol {

    private var fabExpanded = false
    private lateinit var menuButton: FloatingActionMenu
    private lateinit var topupButton: FloatingActionButton
    private lateinit var myQrButton: FloatingActionButton
    private lateinit var sendButton: FloatingActionButton
    private lateinit var tokenSaleButton: FloatingActionButton
    private lateinit var unclaimedGASLabel: TickerView
    private lateinit var claimButton: Button
    private lateinit var claims: Claims
    private var currentAccountState: AccountState? = null
    private lateinit var neoBalance: Balance
    private lateinit var gasBalance: Balance
    private lateinit var swipeContainer: SwipeRefreshLayout
    private lateinit var assetListView: ListView
    private lateinit var claimToast: Toast
    private var isClaiming = false
    var assets: ArrayList<AccountAsset> = arrayListOf<AccountAsset>()


    fun setClaiming(claiming:Boolean) {
        isClaiming = claiming
        this.claimButton.isEnabled = !claiming
        if (claiming) {
            claimProgress.visibility = View.VISIBLE
        } else {
            claimProgress.visibility = View.INVISIBLE
        }
        //finished claiming but have user wait 5 minutes
        if (waitingForClaimTextView != null) {
            this.claimButton.isEnabled = false
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.wallet_fragment_account, container, false)
    }

    fun setupActionButton(view: View) {
        menuButton = view.findViewById<FloatingActionMenu>(R.id.menuActionButton)
        topupButton = view.findViewById<FloatingActionButton>(R.id.fab_cold_storage)
        myQrButton = view.findViewById<FloatingActionButton>(R.id.fab_my_address)
        sendButton = view.findViewById<FloatingActionButton>(R.id.fab_send)
        tokenSaleButton = view.findViewById<FloatingActionButton>(R.id.fab_token_sale)

        tokenSaleButton.colorNormal = resources.getColor(R.color.colorAccent)
        tokenSaleButton.colorPressed = resources.getColor(R.color.colorAccentLight)
        tokenSaleButton.setOnClickListener { showTokenSale() }

        topupButton.colorNormal = resources.getColor(R.color.colorAccent)
        topupButton.colorPressed = resources.getColor(R.color.colorAccentLight)
        topupButton.setOnClickListener { showTopup() }

        myQrButton.colorNormal = resources.getColor(R.color.colorAccent)
        myQrButton.colorPressed = resources.getColor(R.color.colorAccentLight)
        myQrButton.setOnClickListener { showMyAddress() }

        sendButton.colorNormal = resources.getColor(R.color.colorAccent)
        sendButton.colorPressed = resources.getColor(R.color.colorAccentLight)
        sendButton.setOnClickListener { menuButtonTapped() }

        menuButton.menuButtonColorNormal = resources.getColor(R.color.colorAccent)
        menuButton.menuButtonColorPressed = resources.getColor(R.color.colorAccentLight)
        menuButton.transitionName = "reveal"
        menuButton.bringToFront()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        claimProgress.visibility = View.INVISIBLE
        claimButton = view.findViewById<Button>(R.id.claimButton)
        unclaimedGASLabel = view.findViewById(R.id.unclaimedGASLabel)
        assetListView = view.findViewById<ListView>(R.id.assetListView)
        unclaimedGASLabel.setCharacterList(TickerUtils.getDefaultNumberList());

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

        setupActionButton(view)
        try {
            val muli = ResourcesCompat.getFont(view.context, R.font.muli_bold)
            unclaimedGASLabel.typeface = muli
        } catch (e: Resources.NotFoundException) {

        }



        unclaimedGASLabel.text = "0.00000000"
        claimButton.setOnClickListener { claimGasTapped() }

        //menuButton.transitionName = "reveal"
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

    private fun showTopup() {
        //TODO: ADJUST THIS LOGIC
        if (PersistentStore.getColdStorageEnabledStatus() == false) {
            val topupIntent = Intent(context, TopupTutorial::class.java)
            startActivity(topupIntent)
        } else {
            val topupIntent = Intent(context, TopupColdStorageBalanceActivity::class.java)
            startActivity(topupIntent)
        }
    }

    private fun showTokenSale() {
        val tokenSaleIntent = Intent(context, TokenSalesActivity::class.java)
        startActivity(tokenSaleIntent)
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
        for (balance in accountState!!.balances.iterator()) {
            //NEO
            if (balance.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                this.neoBalance = balance
                this.enableClaimGASButton(balance.value)

            } else if (balance.asset.contains(NeoNodeRPC.Asset.GAS.assetID())) {
                this.gasBalance = balance
            }
        }

        assets.clear()
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
            var asset =  AccountAsset(assetID = token.tokenHash,
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
        //TODO: PHASE THIS OUT EVENTUALLY, NEEDED FOR DATA MODEL FIX
        if (PersistentStore.getNeedClearTokens() == true) {
            PersistentStore.removeAllTokens()
            PersistentStore.setNeedClearTokens(false)
        }
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
                onUiThread {
                    this.claimButton.isEnabled = false
                }
            } else {
                onUiThread {
                    this.claims = data!!
                    val amount = data!!.total_unspent_claim / 100000000.0
                    unclaimedGASLabel.text = "%.8f".format(amount)

                    if (isClaiming) {
                        this.claimButton.isEnabled = false
                    } else {
                        this.claimButton.isEnabled = if (amount == 0.0 || unclaimedGASLabel.visibility == View.GONE) false else true
                    }
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

    fun performClaimGAS() {
        NeoNodeRPC(PersistentStore.getNodeURL()).claimGAS(Account.getWallet()!!) {
            onUiThread {
                var success = it.first
                var error = it.second
                setClaiming(false)
                if (success == true) {
                    claimToast.cancel()
                    context!!.toast(resources.getString(R.string.claimed_gas_successfully))
                    disableGasInfo()
                    loadAccountState()
                    loadClaimableGAS()
                    var r = Runnable { kotlin.run {
                        enableGasInfo()
                    } }
                    Handler().postDelayed(r, 60000)
                } else {

                }
            }
        }
    }

    fun performCheckBeforeClaim() {
        onUiThread {
            claimButton.isEnabled = false
        }
        CoZClient().getClaims(Account.getWallet()!!.address) {
            val claims = it.first
            val error = it.second
            if (error != null) {
                onUiThread {
                    setClaiming(false)
                    context?.toast(error!!.message!!)
                    claimToast.cancel()
                }
            } else if (claims?.claims?.size == 0) {
                Thread({
                    Thread.sleep(5000)
                    performCheckBeforeClaim()
                }).run()
            } else {
                onUiThread {
                    performClaimGAS()
                }
            }
        }
    }

    fun sendNEOToOneSelf(){
        NeoNodeRPC(PersistentStore.getNodeURL()).sendNativeAssetTransaction(Account.getWallet()!!, NeoNodeRPC.Asset.NEO, neoBalance.value, Account.getWallet()!!.address, null) {
            var error = it.second
            var success = it.first
            if (error != null) {
                onUiThread {
                    setClaiming(false)
                    claimToast.cancel()
                    context?.toast(error!!.message!!)
                }
            } else if (success == true) {
                Thread({
                    performCheckBeforeClaim()
                }).run()
            }
        }
    }

    fun claimGasTapped() {
        if (this.currentAccountState == null) {
            context?.toast("Unable to retrieve account details, check settings -> network")
            return
        }
        claimToast = context!!.toastUntilCancel(resources.getString(R.string.claiming_gas))
        setClaiming(true)
        claimProgress.visibility = View.VISIBLE
        claimButton.isEnabled = false
        claimToast.show()
        //avoid double send
        Thread({
            CoZClient().getClaims(Account.getWallet()!!.address) {
                val claims = it.first
                val error = it.second
                if (error != null) {
                    onUiThread {
                        setClaiming(false)
                        context?.toast(error!!.message!!)
                        claimToast.cancel()
                    }
                } else if (claims?.claims?.size == 0) {
                    Thread({
                        sendNEOToOneSelf()
                    }).run()
                } else {
                    //if data is already in the array then we can claim.
                    Thread({
                        performClaimGAS()
                    }).run()
                }
            }
        }).run()
    }

    private fun menuButtonTapped() {
        val intent: Intent = Intent(
                context,
                SendActivity::class.java
        )
        intent.putExtra("assets", assets)
        intent.putExtra("address", "")
        val option = ActivityOptionsCompat.makeSceneTransitionAnimation(this.activity!!, menuButton, ViewCompat.getTransitionName(menuButton))
        ActivityCompat.startActivity(context!!, intent, option.toBundle())
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}