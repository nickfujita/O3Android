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
        updateHeaderFunds()
    }

    fun updateHeaderFunds() {
        val displayType = when(position) {
            0 -> HomeViewModel.DisplayType.HOT
            1 -> HomeViewModel.DisplayType.COMBINED
            2 -> HomeViewModel.DisplayType.COLD
            else -> return
        }

        //var homeModel = ViewModelProviders.of(activity!!).get(HomeViewModel::class.java)
        val fundAmountTextView = view?.findViewById<TextView>(R.id.fundAmountTextView)
        val percentChangeTextView = view?.findViewById<TextView>(R.id.fundChangeTextView)

       /* homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> { data ->
            homeModel?.getAccountState(displayType, refresh = true)?.observe(this, Observer<Pair<Int, Double>> { balance ->
                val currentNeoPrice = homeModel.getCurrentNeoPrice()
                val firstNeoPrice = homeModel.getFirstNeoPrice()
                val currentGasPrice = homeModel.getCurrentGasPrice()
                val firstGasPrice = homeModel.getFirstGasPrice()

                val currentPortfolioValue  = (balance?.first!! * currentNeoPrice +
                                             balance?.second!! * currentGasPrice)
                val initialPortfolioValue  = (balance?.first!! * firstNeoPrice +
                                              balance?.second!! * firstGasPrice)


                unscrubbedDisplayedAmount = currentPortfolioValue
                fundAmountTextView?.text = currentPortfolioValue.formattedCurrencyString(homeModel.getCurrency())

                val percentChange = homeModel?.getPercentChange()
                if (percentChange < 0) {
                    fundChangeTextView?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    fundChangeTextView?.setTextColor(resources.getColor(R.color.colorGain))
                }
                fundChangeTextView?.text = percentChange.formattedPercentString()
            })
        })*/
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
