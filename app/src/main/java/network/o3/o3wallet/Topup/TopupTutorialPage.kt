package network.o3.o3wallet.Topup

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.topup_fragment_tutorial_page.*
import network.o3.o3wallet.O3Wallet
import network.o3.o3wallet.Onboarding.LandingFeatureScroll
import network.o3.o3wallet.R
import org.w3c.dom.Text

/**
 * Created by drei on 1/12/18.
 */

class TopupTutorialPage: Fragment() {
    val imageIds: List<Int> = listOf(R.drawable.first_withdraw, R.drawable.second_withdraw, R.drawable.third_withdraw)

    companion object {
        fun newInstance(position: Int): TopupTutorialPage {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = TopupTutorialPage()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.topup_fragment_tutorial_page, container, false)
        val position = arguments!!.getInt("position")
        view.findViewById<ImageView>(R.id.tutorialImage).setImageResource(imageIds[position])
        view.findViewById<TextView>(R.id.tutorialExplanation).text =
                resources.getStringArray(R.array.TOPUP_tutorial_explanations)[position]
        view.findViewById<TextView>(R.id.tutorialSubtitleTextView).text =
                resources.getStringArray(R.array.TOPUP_tutorial_subtitles)[position]
        view.findViewById<TextView>(R.id.tutorialTitle).text =
               resources.getStringArray(R.array.TOPUP_tutorial_titles)[position]
        return view
    }
}