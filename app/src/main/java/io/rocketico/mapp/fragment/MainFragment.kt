package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenFlexibleItem
import kotlinx.android.synthetic.main.bottom_main.*
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
        return inflater.inflate(R.layout.fragment_main_test2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewPager.adapter = object : FragmentStatePagerAdapter(activity?.supportFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment()
                1 -> HistoryFragment()
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }
        viewPager.currentItem = 0

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