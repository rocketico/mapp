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
import android.widget.ArrayAdapter
import android.widget.ExpandableListView
import android.widget.TextView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.rocketico.core.MarketsInfoHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.model.Currency
import io.rocketico.core.model.Token
import io.rocketico.core.model.TokenType
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
        helpingLoadView.visibility = View.VISIBLE
        prograssBar.visibility = View.VISIBLE

        val rate = RateHelper.getTokenRate(context!!, token.type, RateHelper.getCurrentCurrency(context!!))?.rate

        if (token.isEther) tokensTotal.text = token.balance.toString()
        else {
            val ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, RateHelper.getCurrentCurrency(context!!))?.rate
            tokensTotal.text = RateHelper.convertCurrency(rate!!, ethRate!!, token.balance!!).toString()
        }
        fiatTotal.text = (token.balance!! * rate!!).toString()
        tokenName.text = token.type.codeName
        launchDate.text = token.type.launchDate
        hashingAlgorithm.text = token.type.hashAlgorithm
        networkPower.text = token.type.networkPower
        officialWebsite.text = token.type.officialSite
        available.text = token.type.available.toString()
        support.text = token.type.support.toString()
        blockchain.text = token.type.blockChain

        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment.newInstance(token)
                1 -> HistoryFragment()
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }

        setupExchangesList()
        setupListeners()
        setupCurrencySpinner()
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

                helpingLoadView.visibility = View.GONE
                prograssBar.visibility = View.GONE
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

    private fun setupCurrencySpinner() {
        //todo Debug
        val currency = listOf<String>("${Currency.USD.currencySymbol} - ${Currency.USD.codeName}",
                "${Currency.TEST.currencySymbol} - ${Currency.TEST.codeName}")
        val adapter = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, currency)

        chooseFiatSpinner.adapter = adapter
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