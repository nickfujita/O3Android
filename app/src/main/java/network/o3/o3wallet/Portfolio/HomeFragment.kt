package network.o3.o3wallet.Portfolio

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.robinhood.spark.SparkView
import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.view.ViewPager.*
import android.widget.*
import com.robinhood.spark.animation.MorphSparkAnimator
import kotlinx.android.synthetic.main.activity_main.view.*
import network.o3.o3wallet.*
import network.o3.o3wallet.API.O3.Portfolio


class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    var homeModel: HomeViewModel? = null
    var viewPager: ViewPager? = null
    var chartDataAdapter = PortfolioDataAdapter(FloatArray(0))
    var assetListAdapter: AssetListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater!!.inflate(R.layout.fragment_home, container, false)

        homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        assetListAdapter = AssetListAdapter(this.context, this)
        assetListAdapter?.homeModel = homeModel
        view.findViewById<ListView>(R.id.assetListView).adapter = assetListAdapter
        initiateGraph(view)
        initiateViewPager(view)
        initiateData(view)
        initiateIntervalButtons(view)
        return view
    }

    fun initiateGraph(view: View) {
        val sparkView = view.findViewById<SparkView>(R.id.sparkview)
        sparkView?.sparkAnimator = MorphSparkAnimator()
        sparkView?.adapter = chartDataAdapter
        sparkView?.scrubListener = SparkView.OnScrubListener { value ->
            val name = "android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem
            val header = childFragmentManager.findFragmentByTag(name) as PortfolioHeader
            if (value == null) { //return to original state
                header.view?.findViewById<TextView>(R.id.fundAmountTextView)?.text =
                        header.unscrubbedDisplayedAmount.formattedCurrencyString(homeModel?.getCurrency()!!)
                return@OnScrubListener
            } else {
                (value as Float).toDouble().formattedCurrencyString(homeModel?.getCurrency()!!)
            }
        }
    }

    fun initiateViewPager(view: View) {
        viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(childFragmentManager)
        viewPager?.adapter = portfolioHeaderAdapter
        viewPager?.addOnPageChangeListener(object: SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val displayType = when {
                    position == 0 -> HomeViewModel.DisplayType.HOT
                    position == 1 -> HomeViewModel.DisplayType.COMBINED
                    position == 2 -> HomeViewModel.DisplayType.COLD
                    else -> return
                }
                homeModel?.setDisplayType(displayType)
                updatePortfolioAndTable(true)
            }
        })

        viewPager?.addOnPageChangeListener(object: SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val displayType = when {
                    position == 0 -> HomeViewModel.DisplayType.HOT
                    position == 1 -> HomeViewModel.DisplayType.COMBINED
                    position == 2 -> HomeViewModel.DisplayType.COLD
                    else -> return
                }
                homeModel?.setDisplayType(displayType)
                updatePortfolioAndTable(true)
            }
        })
    }

    fun initiateData(view: View) {
        homeModel?.getAccountState(refresh = true)?.observe(this,  Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> {  data ->
                chartDataAdapter.setData(homeModel?.getPriceFloats())
              //  initiateTableRows(view)
            })
        })
    }

    fun initiateIntervalButtons(view: View) {
        val fiveMinButton = view.findViewById<Button>(R.id.fiveMinInterval)
        val fifteenMinButton = view.findViewById<Button>(R.id.fifteenMinuteInterval)
        val thirtyMinButton = view.findViewById<Button>(R.id.thirtyMinuteInterval)
        val sixtyMinButton = view.findViewById<Button>(R.id.sixtyMinuteInterval)
        val oneDayButton = view.findViewById<Button>(R.id.oneDayInterval)
        val allButton = view.findViewById<Button>(R.id.allInterval)

        selectedButton = fifteenMinButton

        fiveMinButton.setOnClickListener { tappedIntervalButton(fiveMinButton) }
        fifteenMinButton.setOnClickListener { tappedIntervalButton(fifteenMinButton) }
        thirtyMinButton.setOnClickListener { tappedIntervalButton(thirtyMinButton) }
        sixtyMinButton.setOnClickListener { tappedIntervalButton(sixtyMinButton) }
        oneDayButton.setOnClickListener { tappedIntervalButton(oneDayButton) }
        allButton.setOnClickListener { tappedIntervalButton(allButton) }
    }

    fun tappedIntervalButton(button: Button) {
        selectedButton?.setTextAppearance(R.style.IntervalButtonText_NotSelected)
        button?.setTextAppearance(R.style.IntervalButtonText_Selected)
        selectedButton = button
        homeModel?.setInterval(button.tag.toString().toInt())
        updatePortfolioAndTable(true)
    }

    fun updatePortfolioAndTable(refresh: Boolean) {
        val sparkView = view?.findViewById<SparkView>(R.id.sparkview)
        homeModel?.getAccountState(refresh = refresh)?.observe(this,  Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(refresh)?.observe(this, Observer<Portfolio> { _ ->
                chartDataAdapter.setData(homeModel?.getPriceFloats())
                assetListAdapter?.notifyDataSetChanged()
                // updateTableData(false)
                viewPager?.setCurrentItem(viewPager?.currentItem!!)
                val name = "android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem
                val header = childFragmentManager.findFragmentByTag(name) as PortfolioHeader
                header.updateHeaderFunds()
            })
        })
    }
    /*
    fun updateTableData(refresh: Boolean) {
        homeModel?.getPortfolioFromModel(refresh)?.observe(this, Observer<Portfolio> {  data ->
            homeModel?.getAccountState(refresh = refresh)?.observe(this,  Observer<Pair<Int, Double>> { balance ->
                val gasChangeTextView = view?.findViewById<TextView>(R.id.gasChange)
                val neoChangeTextView = view?.findViewById<TextView>(R.id.neoChange)

                val gasAmountTextView = view?.findViewById<TextView>(R.id.gasAmount)
                val neoAmountTextView = view?.findViewById<TextView>(R.id.neoAmount)

                val gasValueTextView = view?.findViewById<TextView>(R.id.gasValue)
                val neoValueTextView = view?.findViewById<TextView>(R.id.neoValue)

                val gasPriceTextView = view?.findViewById<TextView>(R.id.gasPrice)
                val neoPriceTextView = view?.findViewById<TextView>(R.id.neoPrice)


                neoAmountTextView?.text = balance?.first?.toString()
                gasAmountTextView?.text = balance?.second?.formattedGASString()

                val currentNeoPrice = homeModel!!.getCurrentNeoPrice()
                val firstNeoPrice = homeModel!!.getFirstNeoPrice()
                val currentGasPrice = homeModel!!.getCurrentGasPrice()
                val firstGasPrice = homeModel!!.getFirstGasPrice()

                neoValueTextView?.text = (balance?.first!! * currentNeoPrice).formattedCurrencyString(homeModel!!.getCurrency())
                gasValueTextView?.text = (balance?.second!! * currentGasPrice).formattedCurrencyString(homeModel!!.getCurrency())
                neoPriceTextView?.text = currentNeoPrice.formattedCurrencyString(homeModel!!.getCurrency())
                gasPriceTextView?.text = currentGasPrice.formattedCurrencyString(homeModel!!.getCurrency())


                val percentNeoChange = (currentNeoPrice - firstNeoPrice) / firstNeoPrice * 100
                val percentGasChange = (currentGasPrice - firstGasPrice) / firstGasPrice * 100


                if (percentGasChange < 0) {
                    gasChangeTextView?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    gasChangeTextView?.setTextColor(resources.getColor(R.color.colorGain))
                }

                if (percentNeoChange < 0) {
                    neoChangeTextView?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    neoChangeTextView?.setTextColor(resources.getColor(R.color.colorGain))
                }

                neoChangeTextView?.text = percentNeoChange.formattedPercentString()
                gasChangeTextView?.text = percentGasChange.formattedPercentString()
            })
        })
    }

    fun initiateTableRows(view: View) {
        val neoRow = view?.findViewById<TableRow>(R.id.neoRow)
        val gasRow = view?.findViewById<TableRow>(R.id.gasRow)

        neoRow.setOnClickListener {
            val intent = Intent(activity, AssetGraph::class.java)
            intent.putExtra("SYMBOL", "NEO")
            startActivity(intent)
        }

        gasRow.setOnClickListener {
            val intent = Intent(activity, AssetGraph::class.java)
            intent.putExtra("SYMBOL", "GAS")
            startActivity(intent)
        }

        updateTableData(false)

    }*/

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}