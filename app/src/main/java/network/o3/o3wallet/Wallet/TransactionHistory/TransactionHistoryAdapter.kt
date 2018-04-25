package network.o3.o3wallet.Wallet.TransactionHistory

import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import network.o3.o3wallet.API.NeoScan.NeoScanTransactionEntry
import android.widget.TextView
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.NEP5Token
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.O3.O3API
import org.jetbrains.anko.find
import org.jetbrains.anko.textColor
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import android.net.Uri


/**
 * Created by drei on 4/24/18.
 */

class TransactionHistoryAdapter(private var transactionHistoryEntries: MutableList<NeoScanTransactionEntry>,
                                context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TRANSACTION_ENTRY_VIEW = 0
    val LOADING_FOOTER_VIEW = 1
    private var mContext = context
    private var isLoadingAdded = false
    private var availableTokens:Array<NEP5Token> = arrayOf()

    init {
        O3API().getAvailableNEP5Tokens {
            if (it.second != null) {
                return@getAvailableNEP5Tokens
            } else {
                availableTokens = it.first!!
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        if (viewType == TRANSACTION_ENTRY_VIEW) {
            val view = layoutInflater.inflate(R.layout.wallet_transaction_history_row_layout, parent, false)
            return TransactionHistoryAdapter.TransactionViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.wallet_transaction_history_footer, parent, false)
            return TransactionHistoryAdapter.LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = getItemViewType(position)
        if (type == LOADING_FOOTER_VIEW) {
            return
        } else {
            (holder as TransactionViewHolder).bindTransaction(transactionHistoryEntries[position], availableTokens)
        }
    }

    override fun getItemCount(): Int {
        var footerInserted = 0
        if (isLoadingAdded) {
            footerInserted = 1
        }
        return transactionHistoryEntries.count() + footerInserted
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == transactionHistoryEntries.count() && isLoadingAdded) {
            LOADING_FOOTER_VIEW
        } else {
            TRANSACTION_ENTRY_VIEW
        }
    }

    fun addLoadingFooter() {
        isLoadingAdded = true
        notifyDataSetChanged()
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        notifyDataSetChanged()
    }

    fun addAllTransactions(txList: List<NeoScanTransactionEntry>) {
        for (tx in txList) {
            transactionHistoryEntries.add(tx)

        }
        notifyDataSetChanged()
    }

    fun removeAllTransactions() {
        transactionHistoryEntries.clear()
        notifyDataSetChanged()
    }

    class TransactionViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view = v
        private var supportedTokens: Array<NEP5Token> = arrayOf()

        companion object {
            private val TRANSACTION_KEY = "TRANSACTION_KEY"
        }

        fun setTokenName(tx: NeoScanTransactionEntry): Boolean {
            val assetTextView = view.find<TextView>(R.id.assetTextView)
            val token = supportedTokens.find { it.tokenHash.contains(tx.asset) }
            if (tx.asset.contains(NeoNodeRPC.Asset.NEO.assetID())) {
                assetTextView.text = "NEO"
                return false
            } else if (tx.asset.contains(NeoNodeRPC.Asset.GAS.assetID())) {
                assetTextView.text = "GAS"
                return false
            } else if (token != null) {
                assetTextView.text = token.symbol.toUpperCase()
                return true
            } else {
                assetTextView.text = "Unknown Asset"
                return true
            }
        }

        fun bindTransaction(tx: NeoScanTransactionEntry, tokens: Array<NEP5Token>) {
            supportedTokens = tokens

            val toTextView = view.find<TextView>(R.id.toTextView)
            val fromTextView = view.find<TextView>(R.id.fromTextView)
            val amountTextView = view.find<TextView>(R.id.amountTextView)
            val blockTextView = view.find<TextView>(R.id.blockNumberTextView)

            val isTokenAsset = setTokenName(tx)
            val amountToDisplay = if(isTokenAsset) {
                tx.amount / 100000000
            } else {
                tx.amount
            }

            var toNickname = PersistentStore.getContacts().find { it.address == tx.address_to }?.nickname
            if (toNickname == null) {
                toNickname = PersistentStore.getWatchAddresses().find {it.address == tx.address_to}?.nickname
            }

            if (tx.address_to == Account.getWallet()?.address!!) {
                toTextView.text = "To: O3 Wallet"
                amountTextView.text =  "+" + amountToDisplay.removeTrailingZeros()
                amountTextView.textColor = O3Wallet.appContext!!.resources!!.getColor(R.color.colorGain)
            } else if (toNickname != null) {
                toTextView.text = "To: " + toNickname
                amountTextView.text =  "-" + amountToDisplay.removeTrailingZeros()
                amountTextView.textColor = O3Wallet.appContext!!.resources!!.getColor(R.color.colorLoss)
            }else {
                toTextView.text = "To: " + tx.address_to
                amountTextView.text =  "-" + amountToDisplay.removeTrailingZeros()
                amountTextView.textColor = O3Wallet.appContext!!.resources!!.getColor(R.color.colorLoss)
            }


            var fromNickname = PersistentStore.getContacts().find { it.address == tx.address_from }?.nickname
            if (fromNickname == null) {
                fromNickname = PersistentStore.getWatchAddresses().find {it.address == tx.address_from }?.nickname
            }

            if (tx.address_from == Account.getWallet()?.address!!) {
                fromTextView.text = "From: O3 Wallet"
            } else if (fromNickname != null) {
                fromTextView.text = "From: " + fromNickname
            } else {
                fromTextView.text = "From: " + tx.address_from
            }

            blockTextView.text = "Block Number: " + tx.block_height

            view.setOnClickListener {
                val url = "https://neoscan.io/transaction/" + tx.txid
                val i = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(i)
            }
        }
    }

    class LoadingViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view = v

        companion object {
            private val LOADING_KEY = "LOADING_KEY"
        }
    }
}

abstract class PaginationScrollListener(internal var layoutManager: LinearLayoutManager) :
        RecyclerView.OnScrollListener() {

    var totalPageCount: Int = 0
    var isLastPage: Boolean = false
    var isLoading: Boolean = false
    protected abstract fun loadMoreItems()

    override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
        super.onScrolled(recyclerView, dx, dy)

        val visibleItemCount = layoutManager.childCount
        val totalItemCount = layoutManager.itemCount
        val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

        if (!isLoading && !isLastPage) {
            if (visibleItemCount + firstVisibleItemPosition >= totalItemCount
                    && firstVisibleItemPosition >= 0
                    && totalItemCount >= totalPageCount) {
                loadMoreItems()
            }
        }
    }
}
