package network.o3.o3wallet

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import com.robinhood.spark.SparkView
import kotlinx.android.synthetic.main.fragment_home.*
import network.o3.o3wallet.API.O3.O3API
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import NeoNodeRPC
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.util.Log
import network.o3.o3wallet.API.O3.PriceData
import android.arch.lifecycle.ViewModelProviders
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager.*
import android.widget.*
import com.robinhood.spark.animation.MorphSparkAnimator
import com.robinhood.spark.animation.SparkAnimator
import network.o3.o3wallet.API.O3.Portfolio
import network.o3.o3wallet.R.id.viewPager
import com.robinhood.spark.animation.LineSparkAnimator






class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    var homeModel: HomeViewModel? = null
    var viewPager: ViewPager? = null
    var chartDataAdapter = PortfolioDataAdapter(FloatArray(0))

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater!!.inflate(R.layout.fragment_home, container, false)
        viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(childFragmentManager)
        viewPager?.adapter = portfolioHeaderAdapter

        val sparkView = view.findViewById<SparkView>(R.id.sparkview)
        sparkView.sparkAnimator = MorphSparkAnimator()
        sparkView.adapter = chartDataAdapter
        homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)

        homeModel?.getAccountState(refresh = true)?.observe(this,  Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> {  data ->
                chartDataAdapter.setData(homeModel?.getPriceFloats())
                //sparkView.adapter = PortfolioDataAdapter(homeModel?.getPriceFloats())
                initiateTableRows(view)
            })
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


        initiateIntervalButtons(view)
        return view
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
        selectedButton?.setBackgroundResource(R.drawable.bottom_unselected)
        button.setBackgroundResource(R.drawable.bottom_selected)
        selectedButton = button
        homeModel?.setInterval(button.tag.toString().toInt())
        updatePortfolioAndTable(true)
    }

    fun updatePortfolioAndTable(refresh: Boolean) {
        val sparkView = view?.findViewById<SparkView>(R.id.sparkview)
        homeModel?.getAccountState(refresh = refresh)?.observe(this,  Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(refresh)?.observe(this, Observer<Portfolio> { _ ->
                //sparkView?.adapter = PortfolioDataAdapter(homeModel?.getPriceFloats())
                chartDataAdapter.setData(homeModel?.getPriceFloats())
                updateTableData(false)
                viewPager?.setCurrentItem(viewPager?.currentItem!!)
                val name = "android:switcher:" + viewPager?.id + ":" + viewPager?.currentItem
                val header = childFragmentManager.findFragmentByTag(name) as PortfolioHeader
                header.updateHeaderFunds()
            })
        })
    }

    fun updateTableData(refresh: Boolean) {
        homeModel?.getPortfolioFromModel(refresh)?.observe(this, Observer<Portfolio> {  data ->
            view?.findViewById<TextView>(R.id.neoPrice)?.text = data!!.price["neo"]?.averageUSD?.formattedUSDString()
            view?.findViewById<TextView>(R.id.gasPrice)?.text = data!!.price["gas"]?.averageUSD?.formattedUSDString()
            homeModel?.getAccountState(refresh = refresh)?.observe(this,  Observer<Pair<Int, Double>> { balance ->
                view?.findViewById<TextView>(R.id.neoAmount)?.text = balance?.first?.toString()
                view?.findViewById<TextView>(R.id.gasAmount)?.text = balance?.second?.formattedGASString()

                val currentNeoPrice = data!!.price["neo"]?.averageUSD!!
                val firstNeoPrice = data.firstPrice["neo"]?.averageUSD!!
                val currentGasPrice = data!!.price["gas"]?.averageUSD!!
                val firstGasPrice = data.firstPrice["gas"]?.averageUSD!!

                view?.findViewById<TextView>(R.id.neoValue)?.text = (balance?.first!! * currentNeoPrice).formattedUSDString()
                view?.findViewById<TextView>(R.id.gasValue)?.text = (balance?.second!! * currentGasPrice).formattedUSDString()

                val percentNeoChange = (currentNeoPrice - firstNeoPrice) / firstNeoPrice * 100
                val percentGasChange = (currentGasPrice - firstGasPrice) / firstGasPrice * 100

                if (percentGasChange < 0) {
                    view?.findViewById<TextView>(R.id.gasChange)?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    view?.findViewById<TextView>(R.id.gasChange)?.setTextColor(resources.getColor(R.color.colorGain))
                }

                if (percentNeoChange < 0) {
                    view?.findViewById<TextView>(R.id.neoChange)?.setTextColor(resources.getColor(R.color.colorLoss))
                } else {
                    view?.findViewById<TextView>(R.id.neoChange)?.setTextColor(resources.getColor(R.color.colorGain))
                }



                view?.findViewById<TextView>(R.id.neoChange)?.text = percentNeoChange.formattedPercentString()
                view?.findViewById<TextView>(R.id.gasChange)?.text = percentGasChange.formattedPercentString()
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

    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}