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
import android.support.v4.view.ViewPager.*
import android.widget.*
import network.o3.o3wallet.API.O3.Portfolio
import network.o3.o3wallet.R.id.viewPager




class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    var homeModel: HomeViewModel? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        val view = inflater!!.inflate(R.layout.fragment_home, container, false)
        val viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(childFragmentManager)
        viewPager.addOnPageChangeListener(object: SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
             //   (portfolioHeaderAdapter.getItem(position) as PortfolioHeader).updateHeaderFunds()
            }
        })
        viewPager.adapter = portfolioHeaderAdapter


        val sparkView = view.findViewById<SparkView>(R.id.sparkview)
        homeModel = ViewModelProviders.of(activity).get(HomeViewModel::class.java)

        homeModel?.getAccountState()?.observe(this,  Observer<Pair<Int, Double>> { balance ->
            homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> {  data ->
                sparkView.adapter = PortfolioDataAdapter(homeModel?.getPriceFloats())
                initiateTableRows(view)
            })
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
        val sparkView = view?.findViewById<SparkView>(R.id.sparkview)
        homeModel?.getPortfolioFromModel(true)?.observe(this, Observer<Portfolio> {  _ ->
            sparkView?.adapter = PortfolioDataAdapter(homeModel?.getPriceFloats())
            updateTableData()
        })
    }

    fun updateTableData() {
        homeModel?.getPortfolioFromModel(false)?.observe(this, Observer<Portfolio> {  data ->
            view?.findViewById<TextView>(R.id.neoPrice)?.text = data!!.price["neo"]?.averageUSD.toString()
            view?.findViewById<TextView>(R.id.gasPrice)?.text = data!!.price["gas"]?.averageUSD.toString()
            homeModel?.getAccountState()?.observe(this,  Observer<Pair<Int, Double>> { balance ->
                view?.findViewById<TextView>(R.id.neoAmount)?.text = balance?.first.toString()
                view?.findViewById<TextView>(R.id.gasAmount)?.text = balance?.second.toString()

                val currentNeoPrice = data!!.price["neo"]?.averageUSD!!
                val firstNeoPrice = data.firstPrice["neo"]?.averageUSD!!
                val currentGasPrice = data!!.price["gas"]?.averageUSD!!
                val firstGasPrice = data.firstPrice["gas"]?.averageUSD!!

                view?.findViewById<TextView>(R.id.neoValue)?.text = (balance?.first!! * currentNeoPrice).toString()
                view?.findViewById<TextView>(R.id.gasValue)?.text = (balance?.second!! * currentGasPrice).toString()

                val percentNeoChange = (currentNeoPrice - firstNeoPrice) / firstNeoPrice * 100
                val percentGasChange = (currentGasPrice - firstGasPrice) / firstGasPrice * 100

                view?.findViewById<TextView>(R.id.neoChange)?.text = percentNeoChange.toString()
                view?.findViewById<TextView>(R.id.gasChange)?.text = percentGasChange.toString()
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

        updateTableData()

    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}