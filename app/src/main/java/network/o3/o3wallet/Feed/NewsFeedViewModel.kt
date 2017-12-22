package network.o3.o3wallet.Feed

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import network.o3.o3wallet.API.O3.FeedData
import network.o3.o3wallet.API.O3.O3API

/**
 * Created by drei on 12/21/17.
 */

class NewsFeedViewModel: ViewModel() {
    var feedData: MutableLiveData<FeedData>? = null

    fun getFeedData(refresh: Boolean): LiveData<FeedData> {
        if (feedData == null || refresh) {
            feedData = MutableLiveData()
            loadFeedData()
        }
        return feedData!!
    }

    fun loadFeedData() {
        O3API().getNewsFeed {
            if ( it?.second != null ) return@getNewsFeed
            feedData?.postValue(it?.first!!)
        }
    }
}