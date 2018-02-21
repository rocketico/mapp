package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.header_main.*

class MainFragment : Fragment() {

    private lateinit var mainFragmentListener: MainFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainFragmentListener = activity as MainFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        viewPager.adapter = object : FragmentStatePagerAdapter(activity.supportFragmentManager) {
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
        menuImageButton.setOnClickListener {
            mainFragmentListener.onMenuButtonClick()
        }

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))
    }

    interface MainFragmentListener {
        fun onMenuButtonClick()
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}