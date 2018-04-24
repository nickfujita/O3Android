package network.o3.o3wallet.Wallet.TransactionHistory

import android.accounts.Account
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import network.o3.o3wallet.API.NeoScan.NeoScanClient
import network.o3.o3wallet.API.NeoScan.NeoScanTransactionEntry
import network.o3.o3wallet.API.NeoScan.NeoScanTransactionHistory
import network.o3.o3wallet.R
import network.o3.o3wallet.TokenSales.TokenSalesAdapter
import android.graphics.Movie






/**
 * Created by drei on 4/24/18.
 */

class TransactionHistoryAdapter(private var transactionHistoryEntries: MutableList<NeoScanTransactionEntry>,
                                context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val TRANSACTION_ENTRY_VIEW = 0
    val LOADING_FOOTER_VIEW = 1
    private var mContext = context
    private var isLoadingAdded = false

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent?.context)
        if (viewType == TRANSACTION_ENTRY_VIEW) {
            val view = layoutInflater.inflate(R.layout.tokensale_info_row, parent, false)
            return TransactionHistoryAdapter.TransactionViewHolder(view)
        } else {
            val view = layoutInflater.inflate(R.layout.settings_add_address_row, parent, false)
            return TransactionHistoryAdapter.LoadingViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder?, position: Int) {
        val type = getItemViewType(position)
        if (type == LOADING_FOOTER_VIEW) {
            return
        } else {
            (holder as TransactionViewHolder).bindTransaction(transactionHistoryEntries[position])
        }
    }

    override fun getItemCount(): Int {
        if (isLoadingAdded) {
            return transactionHistoryEntries.count() + 1
        }
        return transactionHistoryEntries.count()

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
        notifyItemInserted(transactionHistoryEntries.count())
    }

    fun removeLoadingFooter() {
        isLoadingAdded = false
        notifyItemRemoved(transactionHistoryEntries.count())
    }

    fun addAllTransactions(txList: List<NeoScanTransactionEntry>) {
        for (tx in txList) {
            transactionHistoryEntries.add(tx)

        }
        notifyDataSetChanged()
    }

    class TransactionViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        private var view = v

        companion object {
            private val TRANSACTION_KEY = "TRANSACTION_KEY"
        }

        fun bindTransaction(tx: NeoScanTransactionEntry) {

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
