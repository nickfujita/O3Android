package network.o3.o3wallet.Wallet

import android.app.Dialog
import android.os.Bundle
import android.content.DialogInterface
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.API.NEO.NEP5Token
import network.o3.o3wallet.R


class NEP5ListFragment() : BottomSheetDialogFragment() {

    public var delegate: TokenListProtocol? = null
    private lateinit var listView: ListView
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_nep5_list,null)
        dialog!!.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater!!.inflate(R.layout.fragment_nep5_list, container, false)
        listView = view.findViewById<ListView>(R.id.nep5TokenListView)

        var list = ArrayList<NEP5Token>()
        var rpx = NEP5Token(assetID = "ecc6b20d3ccac1ee9ef109af5a7cdb85706b1df9",
                name = "Red Pulse Token",
                symbol = "RPX",
                decimal = 8,
                totalSupply = 1358371250)
        list.add(rpx)

        var dbc = NEP5Token(assetID = "b951ecbbc5fe37a9c280a76cb0ce0014827294cf",
                name = "DeepBrain Coin",
                symbol = "DBC",
                decimal = 8,
                totalSupply = 9580000000.toInt())
        list.add(dbc)

        var rht = NEP5Token(assetID = "2328008e6f6c7bd157a342e789389eb034d9cbc4",
                name = "Redeemable HashPuppy Token",
                symbol = "RHT",
                decimal = 0,
                totalSupply = 60000)
        list.add(rht)

        var qlc = NEP5Token(assetID = "0d821bd7b6d53f5c2b40e217c6defc8bbe896cf5",
                name = "Qlink Token",
                symbol = "QLC",
                decimal = 8,
                totalSupply = 600000000)
        list.add(qlc)

        listView.adapter = NEP5TokenListAdapter(context, list)
        return view
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        if (delegate != null) {
            delegate!!.reloadTokenList()
        }

    }
}
