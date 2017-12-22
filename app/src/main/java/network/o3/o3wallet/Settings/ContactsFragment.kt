package network.o3.o3wallet.Settings

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v4.view.ViewCompat
import android.support.v7.app.AlertDialog
import android.view.*
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import network.o3.o3wallet.Contact
import network.o3.o3wallet.PersistentStore
import network.o3.o3wallet.R
import android.view.MenuInflater
import network.o3.o3wallet.SendActivity


/**
 * Created by drei on 12/11/17.
 */

class ContactsFragment : BottomSheetDialogFragment() {
    var adapter: ContactsAdapter? = null

    @SuppressLint("RestrictedApi")
    override fun setupDialog(dialog: Dialog, style: Int) {
        super.setupDialog(dialog, style)
        val contentView = View.inflate(context, R.layout.fragment_contacts, null)
        dialog.setContentView(contentView)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_contacts, container, false)
        val headerView = layoutInflater.inflate(R.layout.settings_header, null)
        headerView.findViewById<TextView>(R.id.headerTextView).text = "Contacts"

        val listView = view.findViewById<ListView>(R.id.contactsListView)
        listView.addHeaderView(headerView)

        adapter = ContactsAdapter(this.context!!, this)
        listView?.adapter = adapter
        return view
    }

    fun showRemoveAlert(contact: Contact) {
        val simpleAlert = AlertDialog.Builder(this.activity!!).create()
        simpleAlert.setTitle("Remove Contact")
        simpleAlert.setMessage("Are you sure you want to remove this contact?")

        simpleAlert.setButton(AlertDialog.BUTTON_POSITIVE, "OK", {
            dialogInterface, i ->
            PersistentStore.removeContact(contact.address, contact.nickname)
            adapter?.updateData()
        })

        simpleAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", {
            dialogInterface, i ->
        })
        simpleAlert.show()
    }

    fun sendToAddress(contact: Contact) {
        val intent: Intent = Intent(
                context,
                SendActivity::class.java
        )
        intent.putExtra("address",contact.address)
        ActivityCompat.startActivity(context!!, intent, null)
    }

    val RELOAD_DATA = 1
    fun addNewAddress() {
        val intent = Intent(context, AddContact::class.java)
        startActivityForResult(intent,RELOAD_DATA)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == RELOAD_DATA) {
            adapter!!.updateData()
        }
    }

    companion object {
        fun newInstance(): ContactsFragment {
            return ContactsFragment()
        }
    }
}