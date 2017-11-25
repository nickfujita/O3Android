package network.o3.o3wallet.API.CoZ

/**
 * Created by drei on 11/24/17.
 */
data class TransactionHistoryEntry(val GAS: Double,
                                   val NEO: Double,
                                   val block_index: Int,
                                   val gas_sent: Boolean,
                                   val neo_sent: Boolean,
                                   val txid: String
                                   )

data class TransactionHistory(val address: String,
                              val history: Array<TransactionHistoryEntry>,
                              val name: String,
                              val net: String)

data class Claim(val claim: Int,
                 val end: Int,
                 val index: Int,
                 val start: Int,
                 val sysfee: Int,
                 val txid: String,
                 val value: Int)

data class Claims(val address: String,
                  val claims: Array<Claim>,
                  val net: String,
                  val total_claim: Int,
                  val total_unspent_claim: Int)