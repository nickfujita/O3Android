package network.o3.o3wallet.ui.Account

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.support.v4.widget.SwipeRefreshLayout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import network.o3.o3wallet.API.CoZ.CoZClient
import network.o3.o3wallet.Account
import network.o3.o3wallet.R


class AccountTransactionsFragment : android.support.v4.app.Fragment() {

    private lateinit var transactionListView: ListView
    private lateinit var swipeContainer: SwipeRefreshLayout


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_account_transactions, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //view here
        transactionListView = view!!.findViewById<ListView>(R.id.transactionListView)
        swipeContainer = view!!.findViewById<SwipeRefreshLayout>(R.id.swipeContainer)
        transactionListView.emptyView = view!!.findViewById<TextView>(R.id.emptyTransaction)
        swipeContainer.setColorSchemeResources(R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary,
                R.color.colorPrimary)

        swipeContainer.setOnRefreshListener {
            swipeContainer.isRefreshing = true
            this.loadTransactionHistory()
        }
        this.loadTransactionHistory()
    }

    fun loadTransactionHistory() {
        CoZClient().getTransactionHistory(address = Account.getWallet()!!.address) {
            var error = it.second
            var data = it.first
            if (error != null) {
                activity.runOnUiThread {
                    swipeContainer.isRefreshing = false
                }
            } else {
                activity.runOnUiThread {
                    val adapter = TransactionHistoryAdapter(activity as Context, data!!.history)
                    transactionListView.adapter = adapter
                    swipeContainer.isRefreshing = false
                }
            }
        }
    }

    companion object {
        fun newInstance(): AccountTransactionsFragment {
            return AccountTransactionsFragment()
        }
    }
}