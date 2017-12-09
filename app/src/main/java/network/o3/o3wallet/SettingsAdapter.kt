package network.o3.o3wallet

import android.R.attr.*
import android.content.Context
import android.widget.ArrayAdapter
import android.R.string.ok
import android.R.string.no
import android.widget.TextView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import org.w3c.dom.Text


/**
 * Created by drei on 12/8/17.
 */

class SettingsAdapter(context: Context): BaseAdapter() {

    private val mContext: Context
    var settingsTitles = listOf<String>("My Private Key", "Address Book", "Watch-Only-Address",
            "Network", "Theme", "Share", "Contact", "Log out", "Version")
    var images =  listOf(R.drawable.ic_settingsprivatekeyicon, R.drawable.ic_settingsaddressbookicon,
                        R.drawable.ic_settingswatchonlyaddressicon, R.drawable.ic_settingsnetworkicon,
                        R.drawable.ic_settingsnetworkicon, R.drawable.ic_settingsshareicon,
                        R.drawable.ic_settingscontacticon, R.drawable.ic_settingscontacticon,
                        R.drawable.ic_settingscontacticon)
    init {
        mContext = context
    }

    override fun getItem(position: Int): Pair<String, Int> {
        return Pair(settingsTitles[position], images[position])
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return settingsTitles.count()
    }

    override fun getView(position: Int, convertView: View?, viewGroup: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.row_layout, viewGroup, false)
        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val subtitleTextView = view.findViewById<TextView>(R.id.subTitleTextView)
        titleTextView.text = getItem(position).first
        subtitleTextView.text = ""
        view.findViewById<ImageView>(R.id.settingsIcon).setImageResource(getItem(position).second)
        return view


    }


    /*override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        var inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = inflater!!.inflate(R.layout.row_layout, parent)
        val titleTextView = view.findViewById<TextView>(R.id.titleTextView)
        val subTitleTextView = view.findViewById<TextView>(R.id.titleTextView)
        titleTextView.text = objects[position]
        subTitleTextView.text = objects[position]
        return view
    }*/
}