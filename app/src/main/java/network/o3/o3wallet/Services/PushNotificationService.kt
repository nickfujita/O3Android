package network.o3.o3wallet.Services

import android.app.IntentService
import android.content.Intent
import co.getchannel.channel.Channel
import co.getchannel.channel.callback.ChannelCallback
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId


/**
 * Created by apisit on 12/20/17.
 */
class PushNotificationService : FirebaseInstanceIdService() {

    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        if (refreshedToken != null) {
            Channel.saveDeviceToken(refreshedToken,object : ChannelCallback {
                override fun onSuccess() {

                }
                override fun onFail(message: String) {

                }
            })
        }
    }
}