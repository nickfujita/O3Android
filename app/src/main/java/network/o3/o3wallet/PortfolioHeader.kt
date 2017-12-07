package network.o3.o3wallet

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.fragment_home.*
import org.w3c.dom.Text


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [PortfolioHeader.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [PortfolioHeader.newInstance] factory method to
 * create an instance of this fragment.
 */
class PortfolioHeader:Fragment() {
    val titles: List<String> = listOf("OZONE WALLET", "COMBINED", "COLD STORAGE")
    val amounts: List<String> = listOf("0.1BTC", "1.33BTC", "1.23BTC")
    val changes: List<String> = listOf("+2.5%", "+2.5%", "+2.5%")
    companion object {
        fun newInstance(position: Int): PortfolioHeader {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = PortfolioHeader()
            fragment.arguments = args
            //fragment.updateHeaderFunds(position)
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_portfolio_header, container, false)
        val position = arguments.getInt("position")
        val fundSourceTextView = view?.findViewById<TextView>(R.id.fundSourceTextView)
        fundSourceTextView?.text = titles[position]


        val fundChangeTextView = view?.findViewById<TextView>(R.id.fundChangeTextView)
        fundChangeTextView?.text = changes[position]

        configureArrows(view)
        return view
    }

    fun updateHeaderFunds(position: Int) {
        var parent = parentFragment as HomeFragment
        parent.headerPosition = position
      //  parent.updatePortfolio()

        val fundAmountTextView = view?.findViewById<TextView>(R.id.fundAmountTextView)
        if ( parent.isPricedInBTC ) {
            fundAmountTextView?.text = parent.latestPrice?.averageBTC.toString()
        } else {
            fundAmountTextView?.text = parent.latestPrice?.averageUSD.toString()
        }
    }

    fun configureArrows(view: View?) {
        val pager = activity.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val leftArrow = view?.findViewById<ImageView>(R.id.leftArrowImageView)
        val rightArrow = view?.findViewById<ImageView>(R.id.rightArrowImageView)

        leftArrow?.setOnClickListener() {
            pager?.setCurrentItem(pager?.currentItem - 1)
        }

        rightArrow?.setOnClickListener() {
            pager?.setCurrentItem(pager?.currentItem + 1)
        }
    }
}
