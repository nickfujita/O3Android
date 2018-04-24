package network.o3.o3wallet.Wallet.TransactionHistory
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.DefaultItemAnimator
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import network.o3.o3wallet.API.NeoScan.NeoScanClient
import network.o3.o3wallet.API.NeoScan.NeoScanTransactionHistory
import network.o3.o3wallet.Account
import network.o3.o3wallet.R
import org.jetbrains.anko.find
import org.jetbrains.anko.support.v4.find
import org.jetbrains.anko.support.v4.onUiThread

/**
 * A simple [Fragment] subclass.
 */
class TransactionHistoryFragment : Fragment() {

    lateinit var recyclerView: RecyclerView

    var txHistory: NeoScanTransactionHistory? = null
    var currentPage = 1


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater.inflate(R.layout.wallet_transaction_history_fragment, container, false)
        recyclerView = view.find<RecyclerView>(R.id.txHistoryRecyclerView)
        val entries = txHistory?.entries?.toMutableList() ?: mutableListOf()

        val layoutManager = LinearLayoutManager(this.context, LinearLayoutManager.VERTICAL, false)
        recyclerView.layoutManager = layoutManager
        recyclerView.itemAnimator = DefaultItemAnimator()
        recyclerView.adapter = TransactionHistoryAdapter(entries, context)

        var paginator = object: PaginationScrollListener(layoutManager) {
            override fun loadMoreItems() {
                onUiThread { (recyclerView.adapter as TransactionHistoryAdapter).addLoadingFooter() }
                currentPage = currentPage + 1
                NeoScanClient().getNeoScanTransactionHistory(Account.getWallet()?.address!!, currentPage) {
                    onUiThread { (recyclerView.adapter as TransactionHistoryAdapter).removeLoadingFooter() }
                    if (it.second != null) {
                        currentPage = currentPage - 1
                        return@getNeoScanTransactionHistory
                    }
                    totalPageCount = it.first?.total_pages!!
                    isLastPage = currentPage == totalPageCount
                    onUiThread {
                        (recyclerView.adapter as TransactionHistoryAdapter).addAllTransactions(it.first?.entries?.toList()!!)
                    }
                }
            }
        }

        recyclerView.addOnScrollListener(paginator)
        loadFirstPage()
        return view
    }

    fun loadFirstPage() {
        NeoScanClient().getNeoScanTransactionHistory(Account.getWallet()?.address!!, currentPage) {
            if (it.second != null) {
                currentPage = currentPage - 1
                return@getNeoScanTransactionHistory
            }
            onUiThread {
                (recyclerView.adapter as TransactionHistoryAdapter).addAllTransactions(it.first?.entries?.toList()!!)
            }
        }
    }

    companion object {
        fun newInstance(): Fragment {
            return TransactionHistoryFragment()
        }
    }
}
