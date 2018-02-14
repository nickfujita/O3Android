package network.o3.o3wallet.Portfolio

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.robinhood.spark.SparkView
import android.os.Handler
import android.support.v4.view.ViewPager.*
import android.widget.*
import com.robinhood.spark.animation.MorphSparkAnimator
import kotlinx.android.synthetic.main.portfolio_fragment_home.*
import network.o3.o3wallet.*
import network.o3.o3wallet.API.NEO.AccountAsset
import network.o3.o3wallet.API.O3.Portfolio
import org.jetbrains.anko.support.v4.onUiThread


class HomeFragment : Fragment(), HomeViewModelProtocol {
    var selectedButton: Button? = null
    lateinit var homeModel: HomeViewModel
    var viewPager: ViewPager? = null
    var chartDataAdapter = PortfolioDataAdapter(FloatArray(0))
    var assetListAdapter: AssetListAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        return inflater!!.inflate(R.layout.portfolio_fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        assetListAdapter = AssetListAdapter(this.context!!, this)
        homeModel = HomeViewModel()
        homeModel.delegate = this
        homeModel.loadAssetsFromModel(false)
        view!!.findViewById<ListView>(R.id.assetListView).adapter = assetListAdapter
        initiateGraph(view)
        initiateViewPager(view)
        initiateData(view)
        initiateIntervalButtons(view)
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
                updateHeader(homeModel.getCurrentPortfolioValue().formattedCurrencyString(homeModel.getCurrency()),
                        homeModel.getPercentChange())
                return@OnScrubListener
            } else {
                val scrubbedAmount = (value as Float).toDouble()
                val percentChange = (scrubbedAmount - homeModel?.getInitialPortfolioValue()!!) /
                        homeModel.getInitialPortfolioValue()!! * 100
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
                  //  updatePortfolioAndTable(true)
                }, 200)
            }
        })
    }

    override fun updateBalanceData(assets: ArrayList<AccountAsset>) {
        onUiThread {
            assetListAdapter?.assets = assets
            assetListAdapter?.notifyDataSetChanged()
        }
        homeModel.loadPortfolioValue()
    }

    override fun updatePortfolioData(portfolio: Portfolio) {
        onUiThread {
            assetListAdapter?.portfolio = portfolio
            assetListAdapter?.referenceCurrency = homeModel.getCurrency()
            assetListAdapter?.notifyDataSetChanged()

            updateHeader(homeModel.getCurrentPortfolioValue().formattedCurrencyString(homeModel.getCurrency()),
                    homeModel.getPercentChange())
            chartDataAdapter.setData(homeModel?.getPriceFloats())

        }
    }

    fun initiateData(view: View) {

    //    homeModel?.getAssetsFromModel(true)?.observe(this, Observer<ArrayList<AccountAsset>> { balance ->
            //homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> { data ->
          //      chartDataAdapter.setData(homeModel?.getPriceFloats())
        //        assetListAdapter?.notifyDataSetChanged()
          //  })
        //})
    }

    fun initiateIntervalButtons(view: View) {
        val sixHourButton = view.findViewById<Button>(R.id.sixHourInterval)
        val oneDayButton = view.findViewById<Button>(R.id.oneDayInterval)
        val oneWeekButton = view.findViewById<Button>(R.id.oneWeekInterval)
        val oneMonthButton = view.findViewById<Button>(R.id.oneMonthInterval)
        val threeMonthButton = view.findViewById<Button>(R.id.threeMonthInterval)
        val allButton = view.findViewById<Button>(R.id.allInterval)

        selectedButton = oneDayButton

        sixHourButton.setOnClickListener { tappedIntervalButton(sixHourButton) }
        oneDayButton.setOnClickListener { tappedIntervalButton(oneDayButton) }
        oneWeekButton.setOnClickListener { tappedIntervalButton(oneWeekButton) }
        oneMonthButton.setOnClickListener { tappedIntervalButton(oneMonthButton) }
        threeMonthButton.setOnClickListener { tappedIntervalButton(threeMonthButton) }
        allButton.setOnClickListener { tappedIntervalButton(allButton) }
    }

    fun tappedIntervalButton(button: Button) {
        selectedButton?.setTextAppearance(R.style.IntervalButtonText_NotSelected)
        button?.setTextAppearance(R.style.IntervalButtonText_Selected)
        selectedButton = button
        homeModel.setInterval(button.tag.toString())
        homeModel.loadPortfolioValue()
    }

    fun updateHeader(amount: String, percentChange: Double) {
      /*val progress = view?.findViewById<ProgressBar>(R.id.progressBar)
        progress?.visibility = View.VISIBLE
        val sparkView = view?.findViewById<SparkView>(R.id.sparkview)
     //   homeModel?.getAssetsFromModel(true)?.observe(this, Observer<ArrayList<AccountAsset>> { assets ->
        //    homeModel?.getPortfolioFromModel(refresh)?.observe(this, Observer<Portfolio> { _ ->
                progress?.visibility = View.GONE
            //    chartDataAdapter.setData(homeModel?.getPriceFloats())*/
                viewPager?.setCurrentItem(viewPager?.currentItem!!)
                val name = "android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem
                val header = childFragmentManager.findFragmentByTag(name) as PortfolioHeader
                header.setHeaderInfo(amount, percentChange)
          //  })
       // })
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}