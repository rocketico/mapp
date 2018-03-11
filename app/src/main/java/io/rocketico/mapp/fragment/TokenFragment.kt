package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_token.*
import kotlinx.android.synthetic.main.header_main.*

class TokenFragment : Fragment() {

    private lateinit var listener: TokenFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as TokenFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_token, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        backButton.visibility = View.VISIBLE
        helpingView.visibility = View.VISIBLE

        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment()
                1 -> HistoryFragment()
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }
        setupListeners()
    }

    private fun setupListeners() {
        sliding.addPanelSlideListener(object : SlidingUpPanelLayout.PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {

            }

            override fun onPanelStateChanged(panel: View?, previousState: SlidingUpPanelLayout.PanelState?, newState: SlidingUpPanelLayout.PanelState?) {
                Log.i("SLIDING", sliding.panelState.toString())
                sliding.setScrollableView(recyclerViewHistory)
            }
        })

        backButton.setOnClickListener {
            listener.onBackClick()
        }

        menuImageButton.setOnTouchListener { v, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                listener.onMenuButtonClick()
                true
            } else false

        }

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                sliding.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                sliding.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            }

        })
    }

    interface TokenFragmentListener {
        fun onBackClick()
        fun onMenuButtonClick()
    }

    companion object {
        private const val TOKEN_TYPE = "TokenType"

        fun newInstance(tokenType: String) : TokenFragment {
            val fragment = TokenFragment()
            val args = Bundle()

            args.putString(TOKEN_TYPE, tokenType)
            fragment.arguments = args

            return fragment
        }
    }
}