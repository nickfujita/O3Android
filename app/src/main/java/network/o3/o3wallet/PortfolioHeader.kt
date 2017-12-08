package network.o3.o3wallet

import android.arch.lifecycle.ViewModelProviders
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
import network.o3.o3wallet.API.O3.Portfolio
import org.w3c.dom.Text
import android.arch.lifecycle.Observer


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
    var position: Int = 0
    var viewCreated = false
    companion object {
        fun newInstance(position: Int): PortfolioHeader {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = PortfolioHeader()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_portfolio_header, container, false)
        userVisibleHint = false
        position = arguments.getInt("position")
        val fundSourceTextView = view?.findViewById<TextView>(R.id.fundSourceTextView)
        fundSourceTextView?.text = titles[position]


        val fundChangeTextView = view?.findViewById<TextView>(R.id.fundChangeTextView)
        fundChangeTextView?.text = changes[position]

        configureArrows(view)
        return view
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        updateHeaderFunds()
    }

    fun updateHeaderFunds() {
        val displayType = when {
            position == 0 -> HomeViewModel.DisplayType.HOT
            position == 1 -> HomeViewModel.DisplayType.COMBINED
            position == 2 -> HomeViewModel.DisplayType.COLD
            else -> return
        }

        var homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        val fundAmountTextView = view?.findViewById<TextView>(R.id.fundAmountTextView)
        homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> { data ->
            homeModel?.getAccountState(displayType)?.observe(this, Observer<Pair<Int, Double>> { balance ->
                val currentNeoPrice = data!!.price["neo"]?.averageUSD!!
                val currentGasPrice = data!!.price["gas"]?.averageUSD!!
                fundAmountTextView?.text = (balance?.first!! * currentNeoPrice +
                                            balance?.second!! * currentGasPrice).toString()
            })
        })
    }

    fun configureArrows(view: View?) {
        val pager = activity.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val leftArrow = view?.findViewById<ImageView>(R.id.leftArrowImageView)
        val rightArrow = view?.findViewById<ImageView>(R.id.rightArrowImageView)
        var homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)

        val leftIndex: Int? = if (position > 0) position - 1 else null
        val rightIndex: Int? = if (position < 2) position + 1 else null

        if (leftIndex != null) {
            leftArrow?.setOnClickListener() {
               /* if (leftIndex!! == 1) {
                    homeModel.setDisplayType(HomeViewModel.DisplayType.COMBINED)
                } else {
                    homeModel.setDisplayType(HomeViewModel.DisplayType.HOT)
                }*/
                pager?.setCurrentItem(leftIndex)
            }
        }

        if (rightIndex != null) {
            rightArrow?.setOnClickListener() {
               /* if (rightIndex!! == 1) {
                    homeModel.setDisplayType(HomeViewModel.DisplayType.COMBINED)
                } else {
                    homeModel.setDisplayType(HomeViewModel.DisplayType.COLD)
                }*/
                pager?.setCurrentItem(rightIndex)

            }
        }
    }
}
