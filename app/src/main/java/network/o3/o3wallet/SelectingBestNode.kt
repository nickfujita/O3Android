package network.o3.o3wallet

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.akexorcist.localizationactivity.ui.LocalizationActivity
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import neowallet.Neowallet
import neowallet.SeedNodeResponse
import org.jetbrains.anko.coroutines.experimental.bg

class SelectingBestNode : LocalizationActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.portfolio_activity_selecting_best_node)
        getBestNode()
    }

    fun gotBestNode(node: SeedNodeResponse) {
        PersistentStore.setNodeURL(node.url)
        //close activity and start the main tabbed one fresh
        val intent = Intent(this, MainTabbedActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
    }

    fun getBestNode() {

//        val nodes = "http://seed1.neo.org:10332," +
//                "http://seed2.neo.org:10332," +
//                "http://seed3.neo.org:10332," +
//                "http://seed4.neo.org:10332," +
//                "http://seed5.neo.org:10332," +
//                "http://seed1.cityofzion.io:8080," +
//                "http://seed2.cityofzion.io:8080," +
//                "http://seed3.cityofzion.io:8080," +
//                "http://seed4.cityofzion.io:8080," +
//                "http://seed5.cityofzion.io:8080," +
//                "http://node1.o3.network:10332," +
//                "http://node2.o3.network:10332"

        val nodes = "http://seed3.neo.org:20332"

        async(UI) {
            val data: Deferred<SeedNodeResponse> = bg {
                Neowallet.selectBestSeedNode(nodes)
            }
            gotBestNode(data.await())
        }

    }
}
