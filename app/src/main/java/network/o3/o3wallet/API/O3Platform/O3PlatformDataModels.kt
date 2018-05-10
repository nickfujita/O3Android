package network.o3.o3wallet.API.O3Platform

import com.google.gson.JsonElement
import org.json.JSONObject

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


data class PlatformResponse(val code: Int, val result: JsonElement)

data class Claim(val claim: Int,
                 val end: Int,
                 val index: Int,
                 val start: Int,
                 val sysfee: Int,
                 val txid: String,
                 val value: Int)

data class ClaimData(val data: Claimable)

data class Claimable(val gas: String, val claims: Array<UTXO>)

data class Assets(val data: Array<UTXO>)

data class UTXO(val asset: String, val index: Int, val txid: String, val value: String,  val createdAtBlock: Int)