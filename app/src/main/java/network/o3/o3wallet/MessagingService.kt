package network.o3.o3wallet

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {

        if (message == null) {
            return
        }

        if (message.notification != null) {
            ChannelMessageNotification.notify(baseContext,message!!.notification.title.toString(),0)
        }
    }
}
