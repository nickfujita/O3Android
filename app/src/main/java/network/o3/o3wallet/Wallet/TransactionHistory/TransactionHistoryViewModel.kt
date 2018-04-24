package network.o3.o3wallet.Wallet.TransactionHistory

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import network.o3.o3wallet.API.NeoScan.NeoScanClient
import network.o3.o3wallet.API.NeoScan.NeoScanTransactionHistory
import network.o3.o3wallet.Account

/**
 * Created by drei on 4/24/18.
 */


class TransactionHistory: ViewModel() {
    private var transactionHistory: MutableLiveData<NeoScanTransactionHistory>? = null

    fun getTransactionHistory(refresh: Boolean, page: Int): LiveData<NeoScanTransactionHistory> {
        if (transactionHistory == null || refresh) {
            transactionHistory = MutableLiveData()
            loadTransactionHistory(page)
        }
        return transactionHistory!!
    }

    fun loadTransactionHistory(page: Int) {
        NeoScanClient().getNeoScanTransactionHistory(Account.getWallet()!!.address, page){
            if (it.second != null) {
                return@getNeoScanTransactionHistory
            } else {
                transactionHistory!!.postValue(it.first!!)
            }
        }
    }
}