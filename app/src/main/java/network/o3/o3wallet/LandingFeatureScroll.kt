package network.o3.o3wallet

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView


/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * [LandingFeatureScroll.OnFragmentInteractionListener] interface
 * to handle interaction events.
 * Use the [LandingFeatureScroll.newInstance] factory method to
 * create an instance of this fragment.
 */

class LandingFeatureScroll: Fragment() {
    val imageIds: List<Int> = listOf(R.drawable.chart_line, R.drawable.exchange, R.drawable.lock)
    val titles: List<String> = listOf("Add Watch Only Address", "Login using a private key", "Send, Claim, and Recieve")
    companion object {
        fun newInstance(position: Int): LandingFeatureScroll {
            val args = Bundle()
            args.putInt("position", position)
            val fragment = LandingFeatureScroll()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater?.inflate(R.layout.fragment_landing_feature_scroll, container, false)
        val position = arguments.getInt("position")
        val featureImageView = view?.findViewById<ImageView>(R.id.featureImage)
        val featureTextView = view?.findViewById<TextView>(R.id.featureText)

        featureImageView?.setImageResource(imageIds[position])
        featureTextView?.text = titles[position]

        return view
    }
}