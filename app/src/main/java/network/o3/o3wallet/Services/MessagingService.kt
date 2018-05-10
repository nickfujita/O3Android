package network.o3.o3wallet.Services

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import network.o3.o3wallet.ChannelMessageNotification

class MessagingService : FirebaseMessagingService() {

    override fun onMessageReceived(message: RemoteMessage?) {

        if (message == null) {
            return
        }

        if (message.notification != null) {
            ChannelMessageNotification.notify(baseContext, message.notification.title.toString(), 0)
        }
    }
}
