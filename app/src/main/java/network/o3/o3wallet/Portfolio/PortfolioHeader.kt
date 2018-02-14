package network.o3.o3wallet.Portfolio

import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import network.o3.o3wallet.API.O3.Portfolio
import android.arch.lifecycle.Observer
import kotlinx.android.synthetic.main.portfolio_asset_card.*
import kotlinx.android.synthetic.main.portfolio_fragment_portfolio_header.*
import network.o3.o3wallet.*

class PortfolioHeader:Fragment() {
    private val titles = O3Wallet.appContext!!.resources.getStringArray(R.array.portfolio_headers)
    var position: Int = 0
    var unscrubbedDisplayedAmount = 0.0
    companion object {
        fun newInstance(position: Int): PortfolioHeader {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = PortfolioHeader()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.portfolio_fragment_portfolio_header, container, false)
        position = arguments!!.getInt("position")
        val fundSourceTextView = view?.findViewById<TextView>(R.id.fundSourceTextView)
        fundSourceTextView?.text = titles[position]

        val fundChangeTextView = view?.findViewById<TextView>(R.id.fundChangeTextView)
        fundChangeTextView?.text = ""

        configureArrows(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }

    fun setHeaderInfo(amount: String, percentChange: Double) {
        fundChangeTextView.text = percentChange.formattedPercentString()
        fundAmountTextView.text = amount

        if (percentChange < 0) {
            percentChangeTextView?.setTextColor(resources.getColor(R.color.colorLoss))
        } else {
            percentChangeTextView?.setTextColor(resources.getColor(R.color.colorGain))
        }
    }

    private fun configureArrows(view: View?) {
        val pager = activity?.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val leftArrow = view?.findViewById<ImageView>(R.id.leftArrowImageView)
        val rightArrow = view?.findViewById<ImageView>(R.id.rightArrowImageView)

      /*  var homeModel = ViewModelProviders.of(activity!!).get(HomeViewModel::class.java)
        view?.findViewById<TextView>(R.id.fundAmountTextView)!!.setOnClickListener {
            if (homeModel.getCurrency() == CurrencyType.USD) {
                homeModel.setCurrency(CurrencyType.BTC)
            } else {
                homeModel.setCurrency(CurrencyType.USD)
            }
            (parentFragment as HomeFragment).updatePortfolioAndTable(true)
        }

        if (position > 0) {
            leftArrow?.setOnClickListener {
                pager?.currentItem = position - 1
            }
        }*/


        if (position < 2) {
            rightArrow?.setOnClickListener {
                pager?.currentItem = position + 1
            }
        }
    }
}
