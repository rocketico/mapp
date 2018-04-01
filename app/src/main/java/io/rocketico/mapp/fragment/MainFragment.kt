package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.*
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.*
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenFlexibleItem
import io.rocketico.mapp.event.MainCurrencyEvent
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.header_main.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.math.BigInteger

class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentListener: MainFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    private lateinit var ethHelper: EthereumHelper

    private lateinit var wm: WalletManager
    private lateinit var wallet: Wallet
    private lateinit var currentCurrency: Currency

    private var totalBalance: Float? = 0f
    private var totalFiatBalance: Float? = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as MainFragmentListener
    }

    override fun onResume() {
        super.onResume()
        sliding.panelState = PanelState.COLLAPSED
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        refresher.setColorSchemeColors(resources.getColor(R.color.colorPrimaryDark))
        refresher.isRefreshing = true

        ethHelper = EthereumHelper(Cc.ETH_NODE)
        wm = WalletManager(context!!)
        wallet = wm.getWallet()!!

        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment.newInstance(wallet)
                1 -> HistoryFragment.newInstance(wallet)
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 2
        }

        currentCurrency = RateHelper.getCurrentCurrency(context!!)

        setupRecyclerViews()
        setupListeners()
    }

    private fun setupRecyclerViews() {
        val tokens = mutableListOf<IFlexible<*>>()
        listAdapter = FlexibleAdapter(tokens)
        tokenList.layoutManager = LinearLayoutManager(context)
        tokenList.adapter = listAdapter

        showTokens()
    }

    private fun showTokens() {
        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            updateBalancesAndRates(false)

            view?.context?.runOnUiThread {
                val rates = RateHelper.loadRates(context!!, currentCurrency)?.rates

                //fill ether token
                val ethRate = rates?.find { it.tokenType == TokenType.ETH }?.rate
                val ethBalance = BalanceHelper.loadTokenBalance(context!!, TokenType.ETH)
                val floatEthBalance = ethBalance?.let { Utils.bigIntegerToFloat(it) }

                listAdapter.addItem(TokenFlexibleItem(TokenType.ETH, currentCurrency, floatEthBalance, ethRate))

                //fill other tokens
                wallet.tokens?.forEach {token ->
                    val tokenBalance = BalanceHelper.loadTokenBalance(context!!, token)
                    val floatTokenBalance = tokenBalance?.let { Utils.bigIntegerToFloat(it, token.decimals) }
                    val tokenRate = rates?.find { it.tokenType ==  token}?.rate

                    listAdapter.addItem(TokenFlexibleItem(token, currentCurrency, floatTokenBalance, tokenRate))
                }

                countBalances()
                setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

                refresher.isRefreshing = false
            }
        }
    }

    private fun updateBalancesAndRates(forceUpdate: Boolean) {
        if (io.rocketico.mapp.Utils.isOnline(context!!)){
            if (forceUpdate) {
                RateHelper.deleteAllRates(context!!)
                BalanceHelper.deleteAllBalances(context!!)
            }

            //updating balances and rates
            if (RateHelper.isOutdated(context!!, currentCurrency)) {
                val ratesResponse = loadData { RateHelper.getTokenRateByDate() }
                if (ratesResponse == null) {
                    context?.runOnUiThread { longToast(getString(R.string.server_is_not_available)) }
                }
                ratesResponse?.let { RateHelper.saveRates(context!!, RateHelper.RatesEntity.parse(it))}
            }

            if (BalanceHelper.isTokenBalanceOutdated(context!!, TokenType.ETH)) {
                val ethBalance = loadData { ethHelper.getBalance(wallet.address) }
                ethBalance?.let { BalanceHelper.saveTokenBalance(context!!, TokenType.ETH, it) }
            }

            wallet.tokens?.forEach {token ->
                if (BalanceHelper.isTokenBalanceOutdated(context!!, token)) {
                    val tokenRate = loadData { ethHelper.getBalanceErc20(
                            token.contractAddress,
                            wallet.address,
                            wallet.privateKey
                    ) }
                    tokenRate?.let { BalanceHelper.saveTokenBalance(context!!, token, it) }
                }
            }
        } else {
            context?.runOnUiThread { toast(getString(R.string.no_internet_connection)) }
        }
    }

    private fun setupListeners() {
        menuImageButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                fragmentListener.onMenuButtonClick()
                true
            } else false
        }

        sliding.addPanelSlideListener(object : PanelSlideListener {
            private var prevState: PanelState = PanelState.COLLAPSED

            override fun onPanelSlide(panel: View?, slideOffset: Float) {
            }

            override fun onPanelStateChanged(panel: View?, previousState: PanelState?, newState: PanelState?) {
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
                fragmentListener.onFabClick()
        }

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabs))
        tabs.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPager))

        tabs.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabReselected(tab: TabLayout.Tab?) {
                sliding.panelState = PanelState.EXPANDED
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabSelected(tab: TabLayout.Tab?) {
                sliding.panelState = PanelState.EXPANDED
            }

        })

        listAdapter.addListener(FlexibleAdapter.OnItemClickListener { _, position ->
            val listItem = listAdapter.getItem(position) as TokenFlexibleItem
            fragmentListener.onTokenListItemClick(listItem.tokenType)
            true
        })

        refresher.setOnRefreshListener(this)

        tokensTotal.setOnClickListener(onBalanceClickListener)
        fiatTotal.setOnClickListener(onBalanceClickListener)
    }

    override fun onRefresh() {
        //todo [priority: high] add EventBus event for updating statistics and history
        doAsync {
            updateBalancesAndRates(true)

            view?.context?.runOnUiThread {
                val rates = RateHelper.loadRates(context!!, currentCurrency)?.rates

                val newItems = listAdapter.currentItems
                newItems.forEach { token ->
                    if (token is TokenFlexibleItem) {
                        val tokenRate = rates?.find { it.tokenType ==  token.tokenType }?.rate
                        val tokenBalance = BalanceHelper.loadTokenBalance(context!!, token.tokenType)
                        val floatTokenBalance = tokenBalance?.let {
                            Utils.bigIntegerToFloat(it, token.tokenType.decimals)
                        }

                        token.tokenRate = tokenRate
                        token.tokenBalance = floatTokenBalance
                    }
                }

                countBalances()
                setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

                listAdapter.updateDataSet(newItems)
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

    private fun countBalances() {
        val tokens = listAdapter.currentItems as List<TokenFlexibleItem>
        val ethRate = tokens.find { it.tokenType == TokenType.ETH }?.tokenRate
        run loop@{
            tokens.forEach { token ->
                val balance = token.tokenBalance
                val rate = token.tokenRate

                if (balance == null || rate == null || ethRate == null) {
                    totalBalance = null
                    totalFiatBalance = null
                    return@loop
                } else {
                    totalBalance = totalBalance?.let {
                        it + if (token.tokenType == TokenType.ETH) {
                            balance
                        } else {
                            RateHelper.convertCurrency(rate, ethRate, balance)
                        }
                    }
                    totalFiatBalance = totalFiatBalance?.let { it + balance * rate }
                }
            }
        }
    }

    private fun setHeaderBalances(flag: Boolean) {
        if (flag) {
            tokensTotal.text = context!!.setEthBalance(totalBalance)
            fiatTotal.text = context!!.setBalanceWithCurrency(totalFiatBalance)
        } else {
            tokensTotal.text = context!!.setBalanceWithCurrency(totalFiatBalance)
            fiatTotal.text = context!!.setEthBalance(totalBalance)
        }
    }

    interface MainFragmentListener {
        fun onMenuButtonClick()
        fun onFabClick()
        fun onTokenListItemClick(tokenType: TokenType)
    }

    companion object {
        fun newInstance(): MainFragment {
            val fragment = MainFragment()
            return fragment
        }
    }
}