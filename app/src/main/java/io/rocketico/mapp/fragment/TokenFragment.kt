package io.rocketico.mapp.fragment

import android.annotation.SuppressLint
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
import io.rocketico.core.BalanceHelper
import io.rocketico.core.MarketsInfoHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.response.TokenInfoFromMarket
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.ExpandableListAdapter
import io.rocketico.mapp.adapter.FiatCurrencySpinnerAdapter
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
    private lateinit var tokenType: TokenType
    private var list: List<TokenInfoFromMarket>? = null
    private lateinit var currentCurrency: Currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as TokenFragmentListener
        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_token, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        backButton.visibility = View.VISIBLE
        helpingView.visibility = View.VISIBLE
        helpingLoadView.visibility = View.VISIBLE
        prograssBar.visibility = View.VISIBLE

        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        val rate = RateHelper.getTokenRate(context!!, tokenType, currentCurrency)?.rate
        val balance = Utils.bigIntegerToFloat(BalanceHelper.loadTokenBalance(context!!, tokenType)!!)

        if (tokenType == TokenType.ETH) tokensTotal.text = balance.toString()
        else {
            val ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate
            tokensTotal.text = RateHelper.convertCurrency(rate!!, ethRate!!, balance).toString()
        }
        fiatCurrency.text = currentCurrency.currencySymbol
        fiatTotal.text = (balance * rate!!).toString()
        tokenName.text = tokenType.codeName
        launchDate.text = tokenType.launchDate
        hashingAlgorithm.text = tokenType.hashAlgorithm
        networkPower.text = tokenType.networkPower
        officialWebsite.text = tokenType.officialSite
        availableSupport.text = getString(R.string.available_support_template,
                tokenType.available, tokenType.support)
        blockchain.text = tokenType.blockChain

        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment.newInstance(tokenType)
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
            list = MarketsInfoHelper.getTokenInfoFromMarkets(tokenType.codeName, currentCurrency.codeName)
            list?.forEach { listItemData.add(it.marketName) }

            uiThread {
                markets.setOnChildClickListener { _, v, _, _, _ ->
                    val clickedView = v.findViewById<TextView>(R.id.marketName)
                    val clickedPosition = listItemData.indexOf(listItemData.find { it == clickedView.text }!!)

                    val newState = mutableListOf<String>()
                    newState.add(listItemData.find { it == clickedView.text }!!)

                    for (i in 0 until listItemData.size) {
                        if (listItemData[i] == clickedView.text) continue

                        newState.add(listItemData[i])
                    }

                    fillInfo(clickedPosition)

                    markets.setAdapter(ExpandableListAdapter(context!!, newState))
                    false
                }

                fillInfo(0)
                markets.setAdapter(ExpandableListAdapter(context!!, listItemData))

                helpingLoadView.visibility = View.GONE
                prograssBar.visibility = View.GONE
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun fillInfo(position: Int) {
        val info = list!![position]

        marketCapitalization.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.marketCapitalization)
        lowestRate.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.lowestRate24h)
        highestRate.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.highestRate24h)
        tradingVolume.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.tradingVolume24h)
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
        val currency = Currency.values()
        val adapter = FiatCurrencySpinnerAdapter(context!!, currency)

        chooseFiatSpinner.adapter = adapter
    }

    interface TokenFragmentListener {
        fun onBackClick()
        fun onMenuButtonClick()
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"

        fun newInstance(tokenType: TokenType) : TokenFragment {
            val fragment = TokenFragment()
            val args = Bundle()

            args.putSerializable(TOKEN_TYPE, tokenType)
            fragment.arguments = args

            return fragment
        }
    }
}