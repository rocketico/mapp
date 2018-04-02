package io.rocketico.mapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import io.rocketico.core.*
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.core.model.response.TokenInfoResponse
import io.rocketico.mapp.*
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.ExpandableListAdapter
import io.rocketico.mapp.adapter.FiatCurrencySpinnerAdapter
import io.rocketico.mapp.event.MainCurrencyEvent
import io.rocketico.mapp.event.RefreshEvent
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_token.*
import kotlinx.android.synthetic.main.header_main.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.math.BigInteger

class TokenFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var listener: TokenFragmentListener
    private lateinit var tokenType: TokenType
    private lateinit var list: List<TokenInfoResponse.TokenInfoFromMarket>
    private lateinit var currentCurrency: Currency
    private lateinit var wallet: Wallet

    private var balance: Float? = 0f
    private var rate: Float? = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as TokenFragmentListener
        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet
    }

    override fun onResume() {
        super.onResume()

        sliding.panelState = SlidingUpPanelLayout.PanelState.COLLAPSED
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_token, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        backButton.visibility = View.VISIBLE
        helpingView.visibility = View.VISIBLE
        refresher.isRefreshing = true

        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        rate = RateHelper.getTokenRate(context!!, tokenType, currentCurrency)?.rate
        val balanceBI = BalanceHelper.loadTokenBalance(context!!, tokenType)
        balance =
                if (tokenType == TokenType.ETH) {
                    balanceBI?.let { Utils.bigIntegerToFloat(it) }
                } else {
                    val ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate
                    if (rate != null && ethRate != null && balanceBI != null) {
                        RateHelper.convertCurrency(rate!! , ethRate, Utils.bigIntegerToFloat(balanceBI))
                    } else null
                }

        setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

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
                0 -> StatisticsFragment.newInstance(wallet, tokenType)
                1 -> HistoryFragment.newInstance(wallet, tokenType)
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }

        refresher.setColorSchemeColors(resources.getColor(R.color.colorPrimaryDark))

        setupExchangesList()
        setupListeners()
        setupCurrencySpinner()
    }

    private fun setupExchangesList() {
        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            val marketsInfo = loadData { MarketsInfoHelper.getTokenInfoFromMarkets(tokenType.codeName, currentCurrency.codeName) }
            list = marketsInfo?.marketsInfo ?: listOf(TokenInfoResponse.TokenInfoFromMarket())

            view?.context?.runOnUiThread {
                markets.setOnChildClickListener { _, v, _, _, _ ->
                    val clickedView = v.findViewById<TextView>(R.id.marketName)
                    val clickedPosition = list.indexOf(list.find { it.marketName == clickedView.text }!!)

                    val newState = mutableListOf<TokenInfoResponse.TokenInfoFromMarket>()
                    newState.add(list.find { it.marketName == clickedView.text }!!)

                    for (i in 0 until list.size) {
                        if (list[i].marketName == clickedView.text) continue

                        newState.add(list[i])
                    }

                    fillInfo(clickedPosition)

                    markets.setAdapter(ExpandableListAdapter(context!!, newState))
                    false
                }

                fillInfo(0)
                markets.setAdapter(ExpandableListAdapter(context!!, list))

                refresher.isRefreshing = false
                tokenContent.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fadein))
                tokenContent.visibility = View.VISIBLE
            }
        }
    }

    @SuppressLint("StringFormatMatches")
    private fun fillInfo(position: Int) {
        val info = list[position]

        marketCapitalization.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.marketCapitalization)
        lowestRate.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.lowestRate24h)
        highestRate.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.highestRate24h)
        tradingVolume.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, info.traidingVolume24h)
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

        refresher.setOnRefreshListener(this)

        tokensTotal.setOnClickListener(onBalanceClickListener)
        fiatTotal.setOnClickListener(onBalanceClickListener)
    }

    @SuppressLint("StringFormatMatches")
    override fun onRefresh() {
        EventBus.getDefault().post(RefreshEvent)

        doAsync {
            val newRates = loadData { RateHelper.getTokenRateByDate() }
            newRates?.let { RateHelper.saveRates(context!!, RateHelper.RatesEntity.parse(it)) }

            val ethHelper = EthereumHelper(Cc.ETH_NODE)

            if (tokenType == TokenType.ETH) {
                val balance = loadData { ethHelper.getBalance(wallet.address) }
                balance?.let { BalanceHelper.saveTokenBalance(context!!, tokenType, it) }
            } else {
                val balance = loadData { ethHelper.getBalanceErc20(
                        tokenType.contractAddress,
                        wallet.address,
                        wallet.privateKey) }
                balance?.let { BalanceHelper.saveTokenBalance(context!!, tokenType, it) }
            }

            val marketsInfo = loadData { MarketsInfoHelper.getTokenInfoFromMarkets(tokenType.codeName, currentCurrency.codeName) }
            list = marketsInfo?.marketsInfo ?: listOf(TokenInfoResponse.TokenInfoFromMarket())

            view?.context?.runOnUiThread {
                rate = RateHelper.getTokenRate(context!!, tokenType, currentCurrency)?.rate
                val balanceBI = BalanceHelper.loadTokenBalance(context!!, tokenType)
                balance =
                        if (tokenType == TokenType.ETH) {
                            balanceBI?.let { Utils.bigIntegerToFloat(it) }
                        } else {
                            val ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate
                            if (rate != null && ethRate != null && balanceBI != null) {
                                RateHelper.convertCurrency(rate!! , ethRate, Utils.bigIntegerToFloat(balanceBI))
                            } else null
                        }

                setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

                refresher.isRefreshing = false
            }
        }
    }

    private val onBalanceClickListener = View.OnClickListener {
        val mainCurrency = BalanceHelper.getMainCurrency(context!!)
        setHeaderBalances(!mainCurrency)
        BalanceHelper.setMainCurrency(context!!, !mainCurrency)
        EventBus.getDefault().post(MainCurrencyEvent)
    }

    @SuppressLint("StringFormatMatches")
    private fun setHeaderBalances(flag: Boolean) {
        val fiatBalance =
                if (balance != null && rate != null) balance!! * rate!!
                else null

        if (flag) {
            tokensTotal.text = context!!.setEthBalance(balance)
            fiatTotal.text = context!!.setBalanceWithCurrency(fiatBalance)
        } else {
            tokensTotal.text = context!!.setBalanceWithCurrency(fiatBalance)
            fiatTotal.text = context!!.setEthBalance(balance)
        }
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
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet, tokenType: TokenType) : TokenFragment {
            val fragment = TokenFragment()
            val args = Bundle()

            args.putSerializable(TOKEN_TYPE, tokenType)
            args.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = args

            return fragment
        }
    }
}