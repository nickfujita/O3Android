package network.o3.o3wallet.Settings


import android.annotation.SuppressLint
import android.app.Dialog
import android.os.Bundle
import android.app.Fragment
import android.media.Image
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import net.glxn.qrgen.android.QRCode
import network.o3.o3wallet.Account
import network.o3.o3wallet.R
import network.o3.o3wallet.toHex
import java.security.PrivateKey

class PrivateKeyFragment : BottomSheetDialogFragment() {

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_private_key, null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_private_key, container, false)
        view.findViewById<TextView>(R.id.privateKeyTextView).text = Account.getWallet()?.wif
        val bitmap = QRCode.from(Account.getWallet()!!.wif).withSize(2000, 2000).bitmap()
        view.findViewById<ImageView>(R.id.qrView).setImageBitmap(bitmap)
        return view
    }

    companion object {
        fun newInstance(): PrivateKeyFragment {
            return PrivateKeyFragment()
        }
    }
}
