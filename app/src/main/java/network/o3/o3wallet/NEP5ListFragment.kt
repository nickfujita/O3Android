package network.o3.o3wallet

import android.app.Dialog
import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.app.Fragment
import android.content.DialogInterface
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ListView
import android.widget.TextView
import net.glxn.qrgen.android.QRCode
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.API.NEO.NEP5Token
import network.o3.o3wallet.ui.Account.AccountFragment
import network.o3.o3wallet.ui.Account.TokenListProtocol


class NEP5ListFragment() : BottomSheetDialogFragment() {

    public var delegate: TokenListProtocol? = null
    private lateinit var listView: ListView
    override fun setupDialog(dialog: Dialog?, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context,R.layout.fragment_nep5_list,null)
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

        var aph = NEP5Token(assetID = "a0777c3ce2b169d4a23bcba4565e3225a0122d95",
                name = "Aphelion",
                symbol = "APH",
                decimal = 8,
                totalSupply = 242629235)
        list.add(aph)

        listView.adapter = NEP5TokenListAdapter(context,list)
        return view
    }

    override fun onCancel(dialog: DialogInterface?) {
        super.onCancel(dialog)
        if (delegate != null) {
            delegate!!.reloadTokenList()
        }

    }
}
