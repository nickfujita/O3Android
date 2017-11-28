package network.o3.o3wallet

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

class HomeFragment : Fragment() {
    var selectedButton: Button? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        val view = inflater!!.inflate(R.layout.fragment_home, container, false)
        val portfolioGraph = view.findViewById<SparkView>(R.id.sparkview)
        val dataArray = floatArrayOf(1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F, 9F, 10F)
        portfolioGraph.setAdapter(PortfolioDataAdapter(dataArray))

        val viewPager = view.findViewById<ViewPager>(R.id.portfolioHeaderFragment)
        val portfolioHeaderAdapter = PortfolioHeaderPagerAdapter(activity.supportFragmentManager)
        viewPager.adapter = portfolioHeaderAdapter

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
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}