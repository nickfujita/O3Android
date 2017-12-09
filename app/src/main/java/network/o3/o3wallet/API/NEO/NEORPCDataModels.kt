package network.o3.o3wallet.API.NEO

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName

data class SendRawTransactionResponse(var jsonrpc: String, var id: Int, var result: Boolean)

data class NodeResponse(var jsonrpc: String, var id: Int, var result: JsonObject)

data class ValidatedAddress(val address: String, val isValid: Boolean)

data class Script(val invocation: String, val verification: String)

data class ValueIn(val transactionID: String, val valueOut: Int)

data class ValueOut(val n: Int, val asset: String, val value: String, val address: String)

//TODO FIGURE OUT HANDLING OF SERIALIZED NAMES
data class Transaction(val txid: String,
                  val size: Int,
                  val type: String,
                  val version: Int,
                  val vin: Array<ValueIn>,
                  val vout: Array<ValueOut>,
                  val sys_fee: String,
                  val net_fee: String,
                  val scripts: Array<Script>) {
}

data class Block(val confirmations: Int,
            val hash: String,
            val index: Int,
            val merkleroot: String,
            val nextblockhash: String,
            val nextconsensus: String,
            val nonce: String,
            val previousblockhash: String,
            val size: Int,
            val time: Int,
            val version: Int,
            val script: Script,
            val tx: Array<Transaction>
)

data class Balance(val asset: String,
                   val value: Double)

data class AccountState(val version: Int,
                        val script_hash: String,
                        val frozen: Boolean,
                        val votes: Array<Int>,
                        val balances: Array<Balance>)

