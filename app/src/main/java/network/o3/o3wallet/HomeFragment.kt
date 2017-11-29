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
import java.util.concurrent.TimeUnit

class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_home, container, false)
        val viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(activity.supportFragmentManager)
        viewPager.adapter = portfolioHeaderAdapter

        initiateIntervalButtons(view)
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
        O3API().getPortfolio(2, 2.0, 15) {
            val portfolioGraph = view?.findViewById<SparkView>(R.id.sparkview)
            val data = it.first?.data?.map { it.averageUSD }?.toTypedArray()!!
            var floats = FloatArray(data.count())
            for (i in data.indices) {
                floats[i] = data[i].toFloat()
            }
            portfolioGraph?.setAdapter(PortfolioDataAdapter(floats))
        }
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