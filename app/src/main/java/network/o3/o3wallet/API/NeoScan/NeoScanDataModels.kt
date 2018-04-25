package network.o3.o3wallet.API.NeoScan

/**
 * Created by drei on 4/24/18.
 */

data class NeoScanTransactionHistory(val total_pages: Int, val total_entries: Int,
                                     val page_size: Int, val page_number: Int,
                                     val entries: Array<NeoScanTransactionEntry>)

data class NeoScanTransactionEntry(val txid: String, val time: Int, val id: Int,
                                   val block_height: Int, val asset: String,
                                   val amount: Double, val address_to: String,
                                   val address_from: String)