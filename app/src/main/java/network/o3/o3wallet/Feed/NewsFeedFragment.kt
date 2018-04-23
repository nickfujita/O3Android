package network.o3.o3wallet.Feed

import android.os.Bundle
import android.support.v4.app.Fragment
import android.arch.lifecycle.Observer
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import network.o3.o3wallet.R

class NewsFeedFragment : Fragment() {
    var model: NewsFeedViewModel? = null
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        model = NewsFeedViewModel()

        val view =  inflater!!.inflate(R.layout.news_fragment_news_feed, container, false)
        val listView = view?.findViewById<ListView>(R.id.newsList)
        val featureView = getLayoutInflater().inflate(R.layout.news_fragment_features, null)
        listView?.adapter = NewsFeedAdapter(context!!, this)

        model?.getFeatureData(true)?.observe(this, Observer {features ->
            val featuredRecycler = featureView.findViewById<RecyclerView>(R.id.featuredList)
            featuredRecycler?.adapter = FeaturesAdapter(features = features?.toCollection(ArrayList())!!)
            model?.getFeedData(true)?.observe(this, Observer { feed ->
                (listView!!.adapter as NewsFeedAdapter).setData(feed!!)
                if (features?.isNotEmpty() ?: false) {
                    listView?.addHeaderView(featureView)
                }
            })
        })

        return view
    }


    companion object {
        fun newInstance(): NewsFeedFragment {
            return NewsFeedFragment()
        }
    }
}
