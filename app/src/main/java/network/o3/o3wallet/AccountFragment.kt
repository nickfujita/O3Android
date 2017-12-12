package network.o3.o3wallet

import android.content.res.Resources
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.design.widget.FloatingActionButton
import android.widget.LinearLayout
import kotlinx.android.synthetic.main.fragment_home.*


class AccountFragment : Fragment() {

    private var fabExpanded = false
    private lateinit var menuButton: FloatingActionButton
    private lateinit var sendLayout: LinearLayout
    private lateinit var myAddressLayout: LinearLayout

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                     savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_account, container, false)
    }
    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        menuButton = view!!.findViewById<FloatingActionButton>(R.id.menuActionButton)
        sendLayout = view!!.findViewById<LinearLayout>(R.id.layoutFabSend)
        myAddressLayout= view!!.findViewById<LinearLayout>(R.id.layoutFabMyAddress)
        menuButton.setOnClickListener{ menuButtonTapped() }
    }

    fun menuButtonTapped() {
        if (fabExpanded == true){
            closeMenu();
        } else {
            openMenu();
        }
    }

    fun openMenu() {
        sendLayout.visibility = View.VISIBLE
        myAddressLayout.visibility = View.VISIBLE
        fabExpanded = true
    }

    fun closeMenu() {
        sendLayout.visibility = View.INVISIBLE
        myAddressLayout.visibility = View.INVISIBLE
        fabExpanded = false
    }

    companion object {
        fun newInstance(): AccountFragment {
            return AccountFragment()
        }
    }
}