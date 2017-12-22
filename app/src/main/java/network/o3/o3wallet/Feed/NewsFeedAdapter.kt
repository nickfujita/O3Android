package network.o3.o3wallet.Feed

import android.arch.lifecycle.Observer
import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import network.o3.o3wallet.API.O3.FeedData
import network.o3.o3wallet.API.O3.FeedItem
import network.o3.o3wallet.API.O3.NewsImage
import network.o3.o3wallet.R
import java.net.URI
import android.support.v4.content.ContextCompat.startActivity
import android.content.Intent
import com.bumptech.glide.request.RequestOptions


/**
 * Created by drei on 12/21/17.
 */

class NewsFeedAdapter(context: Context, fragment: NewsFeedFragment): BaseAdapter() {
    var mContext: Context
    private val mFragment: NewsFeedFragment
    private var feedData: FeedData? = null

    init {
        mContext = context
        mFragment = fragment
    }

    fun setData(feed: FeedData) {
        this.feedData = feed
        this.notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItem(position: Int): FeedItem {
        return feedData?.items?.get(position) ?:
                FeedItem("","","","","", arrayOf<NewsImage>())
    }

    override fun getCount(): Int {
        return feedData?.items?.size ?: 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val layoutInflater = LayoutInflater.from(mContext)
        val view = layoutInflater.inflate(R.layout.feed_row, parent, false)
        val feedItem = getItem(position)
        view.findViewById<TextView>(R.id.titleTextView).text = feedItem.title
        view.findViewById<TextView>(R.id.dateTextView).text = feedItem.published
        val imageView = view.findViewById<ImageView>(R.id.newImageView)
        Glide.with(mContext).load(feedItem?.images[0].url).apply(RequestOptions().centerCrop()).into(imageView)

        view?.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(feedItem?.link))
            startActivity(mContext, browserIntent, null)
        }
        return view
    }
}

