package network.o3.o3wallet.Portfolio

import android.app.ProgressDialog
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
import android.os.Handler
import android.support.v4.view.ViewPager.*
import android.view.Window
import android.widget.*
import com.robinhood.spark.animation.MorphSparkAnimator
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.view.*
import kotlinx.android.synthetic.main.fragment_portfolio_header.*
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.NeoNodeRPC
import network.o3.o3wallet.API.O3.Portfolio
import org.jetbrains.anko.coroutines.experimental.bg
import org.jetbrains.anko.support.v4.onUiThread


class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    var homeModel: HomeViewModel? = null
    var viewPager: ViewPager? = null
    var chartDataAdapter = PortfolioDataAdapter(FloatArray(0))
    var assetListAdapter: AssetListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)
        assetListAdapter = AssetListAdapter(this.context, this)
        assetListAdapter?.homeModel = homeModel
        view!!.findViewById<ListView>(R.id.assetListView).adapter = assetListAdapter
        initiateGraph(view!!)
        initiateViewPager(view!!)
        initiateData(view!!)
        initiateIntervalButtons(view!!)
    }

    fun initiateGraph(view: View) {
        val sparkView = view.findViewById<SparkView>(R.id.sparkview)
        sparkView?.sparkAnimator = MorphSparkAnimator()
        sparkView?.adapter = chartDataAdapter
        sparkView?.scrubListener = SparkView.OnScrubListener { value ->
            val name = "android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem
            val header = childFragmentManager.findFragmentByTag(name) as PortfolioHeader

            val amountView = header.view?.findViewById<TextView>(R.id.fundAmountTextView)
            val percentView = header.view?.findViewById<TextView>(R.id.fundChangeTextView)
            if (value == null) { //return to original state
                amountView?.text = header.unscrubbedDisplayedAmount.formattedCurrencyString(homeModel?.getCurrency()!!)
                percentView?.text = homeModel?.getPercentChange()?.formattedPercentString()
                if (homeModel?.getPercentChange()!! < 0) {
                    percentView?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    percentView?.setTextColor(resources.getColor(R.color.colorGain))
                }
                return@OnScrubListener
            } else {
                val scrubbedAmount = (value as Float).toDouble()
                val percentChange = (scrubbedAmount - homeModel?.getInitialPortfolioValue()!!) /
                        homeModel?.getInitialPortfolioValue()!! * 100
                if (percentChange < 0) {
                    percentView?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    percentView?.setTextColor(resources.getColor(R.color.colorGain))
                }
                percentView?.text = percentChange.formattedPercentString()
                amountView?.text = scrubbedAmount.formattedCurrencyString(homeModel?.getCurrency()!!)
            }
        }
    }

    fun initiateViewPager(view: View) {
        viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(childFragmentManager)
        viewPager?.adapter = portfolioHeaderAdapter
        viewPager?.addOnPageChangeListener(object : SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val displayType = when {
                    position == 0 -> HomeViewModel.DisplayType.HOT
                    position == 1 -> HomeViewModel.DisplayType.COMBINED
                    position == 2 -> HomeViewModel.DisplayType.COLD
                    else -> return
                }

                // This delay allows for the scroll to complete before the UI thread gets blocked
                Handler().postDelayed({
                    homeModel?.setDisplayType(displayType)
                    updatePortfolioAndTable(true)
                }, 200)
            }
        })
    }

    fun initiateData(view: View) {
        homeModel?.getAccountState(refresh = true)?.observe(this, Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> { data ->
                chartDataAdapter.setData(homeModel?.getPriceFloats())
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
        val progress = view?.findViewById<ProgressBar>(R.id.progressBar)
        progress?.visibility = View.VISIBLE
        val sparkView = view?.findViewById<SparkView>(R.id.sparkview)
        homeModel?.getAccountState(refresh = refresh)?.observe(this, Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(refresh)?.observe(this, Observer<Portfolio> { _ ->
                progress?.visibility = View.GONE
                chartDataAdapter.setData(homeModel?.getPriceFloats())
                viewPager?.setCurrentItem(viewPager?.currentItem!!)
                val name = "android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem
                val header = childFragmentManager.findFragmentByTag(name) as PortfolioHeader
                header.updateHeaderFunds()
                assetListAdapter?.notifyDataSetChanged()
            })
        })
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}