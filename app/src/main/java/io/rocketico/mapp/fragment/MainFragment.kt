package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenFlexibleItem
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.header_main.*

class MainFragment : Fragment() {

    private lateinit var mainFragmentListener: MainFragmentListener
    lateinit var tokenListAdapter: FlexibleAdapter<IFlexible<*>>
    lateinit var tokens: MutableList<TokenFlexibleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainFragmentListener = activity as MainFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment()
                1 -> HistoryFragment()
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }

        setupListeners()
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        tokens = mutableListOf()
        tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
        tokenList.layoutManager = LinearLayoutManager(context)
        tokenList.adapter = tokenListAdapter

        val itemListener = activity as TokenFlexibleItem.OnItemClickListener

        //TODO for debug
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
        tokenListAdapter.addItem(TokenFlexibleItem(itemListener))
    }

    private fun setupListeners() {
        menuImageButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                mainFragmentListener.onMenuButtonClick()
                true
            } else false
        }

        sliding.addPanelSlideListener(object : PanelSlideListener {
            private var prevState: PanelState = PanelState.COLLAPSED

            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                Log.i("SLIDING", slideOffset.toString())
            }

            override fun onPanelStateChanged(panel: View?, previousState: PanelState?, newState: PanelState?) {
                Log.i("SLIDING", "${newState} ${prevState}")
                if (newState == PanelState.EXPANDED && newState != prevState) {
                    fab.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_stand))
                    fab.visibility = View.GONE
                    prevState = newState
                }
                if (newState == PanelState.COLLAPSED && newState != prevState) {
                    fab.startAnimation(AnimationUtils.loadAnimation(context, R.anim.anim_slide_up))
                    fab.visibility = View.VISIBLE
                    prevState = newState
                }
                sliding.setScrollableView(recyclerViewHistory)
            }
        })

        fab.setOnClickListener {
                mainFragmentListener.onFabClick()
        }

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
    }

    interface MainFragmentListener {
        fun onMenuButtonClick()
        fun onFabClick()
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}