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
import android.widget.ExpandableListView
import android.widget.TextView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.rocketico.core.MarketsInfoHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Token
import io.rocketico.core.model.response.TokenInfoFromMarket
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.ExpandableListAdapter
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_token.*
import kotlinx.android.synthetic.main.header_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*

class TokenFragment : Fragment() {

    private lateinit var listener: TokenFragmentListener
    private lateinit var token: Token
    private var list: List<TokenInfoFromMarket>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as TokenFragmentListener
        token = arguments?.getSerializable(TOKEN) as Token
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_token, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        backButton.visibility = View.VISIBLE
        helpingView.visibility = View.VISIBLE

        if (token.isEther) tokensTotal.text = token.balance.toString()
        else {
            //val ethRate = WalletManager(context!!).getWallet()?.tokens?.find { it.isEther }?.rate
            tokensTotal.text = RateHelper.convertCurrency(token.rate!!, 20f, token.balance!!).toString() //todo get eth token rate
        }
        fiatTotal.text = (token.balance!! * token.rate!!).toString()
        tokenName.text = token.type.codeName



        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment()
                1 -> HistoryFragment()
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }

        setupExchangesList()
        setupListeners()

    }

    private fun setupExchangesList() {
        //todo Debug exchange. please, kill me :*(
        val listItemData = mutableListOf<String>()

        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            list = MarketsInfoHelper.getTokenInfoFromMarkets(token.type.codeName, "USD") //todo currency Debug
            list?.forEach { listItemData.add(it.marketName) }

            uiThread {
                markets.setOnChildClickListener(object : ExpandableListView.OnChildClickListener {
                    override fun onChildClick(parent: ExpandableListView, v: View, groupPosition: Int, childPosition: Int, id: Long): Boolean {
                        val newState = mutableListOf<String>()
                        val clickedView = v.findViewById<TextView>(R.id.marketName)

                        val clickedPosition = listItemData.indexOf(listItemData.find { it == clickedView.text }!!)

                        newState.add(listItemData.find { it == clickedView.text }!!)

                        for (i in 0 until listItemData.size) {
                            if (listItemData[i] == clickedView.text) continue

                            newState.add(listItemData[i])
                        }

                        fillInfo(clickedPosition)

                        markets.setAdapter(ExpandableListAdapter(context!!, newState))
                        return false
                    }
                })

                fillInfo(0)
                markets.setAdapter(ExpandableListAdapter(context!!, listItemData))
            }
        }
    }

    private fun fillInfo(position: Int) {
        marketCapitalization.text = list!![position].marketCapitalization.toString()
        lowestRate.text = list!![position].lowestRate24h.toString()
        highestRate.text = list!![position].highestRate24h.toString()
        tradingVolume.text = list!![position].tradingVolume24h.toString()
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
        private const val TOKEN = "Token"

        fun newInstance(token: Token) : TokenFragment {
            val fragment = TokenFragment()
            val args = Bundle()

            args.putSerializable(TOKEN, token)
            fragment.arguments = args

            return fragment
        }
    }
}