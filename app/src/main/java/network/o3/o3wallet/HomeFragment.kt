package network.o3.o3wallet

import android.os.Bundle
import android.view.ViewGroup
import android.view.LayoutInflater
import android.view.View
import android.support.v4.app.Fragment
import android.widget.TableLayout
import android.widget.TableRow
import com.robinhood.spark.SparkView

class HomeFragment : Fragment() {
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {

        /*val adapter = PortfolioDataAdapter()
        adapter.yData = dataArray
        portfolioGraph.adapter = adapter*/
        return inflater!!.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val portfolioGraph = view!!.findViewById<SparkView>(R.id.sparkview)
        val dataArray = floatArrayOf(1F, 2F, 3F, 4F, 5F, 6F, 7F, 8F, 9F, 10F)
        portfolioGraph.setAdapter(PortfolioDataAdapter(dataArray))
    }

    companion object {
        fun newInstance(): HomeFragment {
            return HomeFragment()
        }
    }
}