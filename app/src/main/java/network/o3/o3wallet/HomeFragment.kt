package network.o3.o3wallet

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.support.v4.view.ViewPager
import android.widget.Button
import android.widget.TableLayout
import android.widget.TableRow
import com.robinhood.spark.SparkView
import kotlinx.android.synthetic.main.fragment_home.*
import network.o3.o3wallet.API.O3.O3API
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import NeoNodeRPC
import android.app.AlertDialog
import android.arch.lifecycle.Observer
import android.util.Log
import android.widget.Toast
import network.o3.o3wallet.API.O3.PriceData
import android.arch.lifecycle.ViewModelProviders




class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    var neoAmountWatchAddresses: Int = 0
    var gasAmountWatchAddresses: Double = 0.0
    var neoAmountO3Address: Int = 0
    var gasAmountO3Address: Double = 0.0
    var headerPosition = 0
    var isPricedInBTC = true
    var latestPrice: PriceData? = null

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_home, container, false)

        val sparkView = view.findViewById<SparkView>(R.id.sparkview)


        val homeModel = ViewModelProviders.of(this).get(HomeViewModel::class.java!!)

        //homeModel.getAccountState().observe(this,  Observer<Pair<Int, Double>> { balance ->
            //Toast.makeText(activity, "data", Toast.LENGTH_LONG).show()
        //})

        homeModel.getPortfolioDataFromModel().observe(this, Observer<FloatArray> {  data ->
            Toast.makeText(activity, "data", Toast.LENGTH_LONG).show()
            sparkView.adapter = PortfolioDataAdapter(data)
        })




        initiateIntervalButtons(view)

        val viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(childFragmentManager)
        viewPager.adapter = portfolioHeaderAdapter
        initiateTableRows(view)

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
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}