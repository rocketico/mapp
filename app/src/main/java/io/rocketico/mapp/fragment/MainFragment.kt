package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
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
import io.rocketico.core.*
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.*
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenFlexibleItem
import io.rocketico.mapp.event.MainCurrencyEvent
import io.rocketico.mapp.event.RefreshEvent
import io.rocketico.mapp.event.UpdateEvent
import kotlinx.android.synthetic.main.include_bottom.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.include_header.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentListener: MainFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    private lateinit var ethHelper: EthereumHelper

    private lateinit var wm: WalletManager
    private lateinit var wallet: Wallet
    private lateinit var currentCurrency: Currency

    private var totalBalance: Float = 0f
    private var totalFiatBalance: Float = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as MainFragmentListener
    }

    override fun onResume() {
        super.onResume()
        sliding.panelState = PanelState.COLLAPSED
    }

    override fun onStop() {
        super.onStop()
        listAdapter.updateDataSet(listAdapter.currentItems)
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
        listAdapter.isSwipeEnabled = true
        listAdapter.itemTouchHelperCallback.setSwipeThreshold(0.3f)
        listAdapter.itemTouchHelperCallback.setSwipeAnimationDuration(200L)

        listAdapter.addItem(TokenFlexibleItem(context!!, TokenType.ETH))

        wallet.tokens?.forEach {token ->
            listAdapter.addItem(TokenFlexibleItem(context!!, token))
        }

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
                EventBus.getDefault().post(UpdateEvent)

                countBalances()
                setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

                listAdapter.updateDataSet(listAdapter.currentItems)
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
                val yesterdayRatesResponse = loadData { RateHelper.getTokenRateByDate(io.rocketico.mapp.Utils.nDaysAgo(1)) }
                if (ratesResponse == null || yesterdayRatesResponse == null) {
                    context?.runOnUiThread { longToast(getString(R.string.server_is_not_available)) }
                }
                ratesResponse?.let { RateHelper.saveRates(context!!, RateHelper.RatesEntity.parse(it))}
                yesterdayRatesResponse?.let { RateHelper.saveYesterdayRates(context!!, RateHelper.RatesEntity.parse(it)) }
            }

            if (BalanceHelper.isTokenBalanceOutdated(context!!, TokenType.ETH)) {
                val ethBalance = loadData { ethHelper.getBalance(wallet.address) }
                ethBalance?.let { BalanceHelper.saveTokenBalance(context!!, TokenType.ETH, it) }
            }

            wallet.tokens?.forEach {token ->
                if (BalanceHelper.isTokenBalanceOutdated(context!!, token)) {
                    val tokenBalance = loadData { ethHelper.getBalanceErc20(
                            token.contractAddress,
                            wallet.address,
                            wallet.privateKey
                    ) }
                    tokenBalance?.let { BalanceHelper.saveTokenBalance(context!!, token, it) }
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

        listAdapter.addListener(object : FlexibleAdapter.OnItemSwipeListener {
            override fun onActionStateChanged(viewHolder: RecyclerView.ViewHolder?, actionState: Int) {

            }

            override fun onItemSwipe(position: Int, direction: Int) {
                val item = listAdapter.getItem(position) as TokenFlexibleItem
                if (item.tokenBalance == null && direction == ItemTouchHelper.RIGHT) {
                    context!!.toast(getString(R.string.no_balance_info))
                    listAdapter.updateItem(item)
                } else {
                    fragmentListener.onTokenListItemSwipe(item.tokenType, position, direction)
                }
            }
        })

        refresher.setOnRefreshListener(this)

        headerMainCurrency.setOnClickListener(onBalanceClickListener)
        headerSecondaryCurrency.setOnClickListener(onBalanceClickListener)
    }

    override fun onRefresh() {
        EventBus.getDefault().post(RefreshEvent)

        doAsync {
            updateBalancesAndRates(true)

            view?.context?.runOnUiThread {
                EventBus.getDefault().post(UpdateEvent)

                countBalances()
                setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

                listAdapter.updateDataSet(listAdapter.currentItems)
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
        totalBalance = 0f
        totalFiatBalance = 0f
        var totalYesterdayFiatBalance = 0f

        val tokens = listAdapter.currentItems as List<TokenFlexibleItem>
        val ethRate = tokens.find { it.tokenType == TokenType.ETH }?.tokenRate
        run loop@{
            tokens.forEach { token ->
                val balance = token.tokenBalance
                val rate = token.tokenRate
                val yesterdayRate = RateHelper.getYesterdayTokenRate(context!!, token.tokenType, currentCurrency)?.rate

                if (token.tokenType == TokenType.ETH) {
                    balance?.let { totalBalance += it }
                } else {
                    val tokenBalance = RateHelper.convertCurrency(rate, ethRate, balance)
                    tokenBalance?.let { totalBalance += it }
                }

                val fiatBalance = balance?.let { rate?.let { balance * rate } }
                fiatBalance?.let { totalFiatBalance += it }

                val yesterdayFiatBalance = balance?.let { yesterdayRate?.let { balance * yesterdayRate } }
                yesterdayFiatBalance?.let { totalYesterdayFiatBalance += it }
            }
        }

        val percentDiff = io.rocketico.mapp.Utils.calculateDifference(totalFiatBalance, totalYesterdayFiatBalance)
        val fiatDiff = totalFiatBalance - totalYesterdayFiatBalance

        percentDiff?.let {
            directionHeader.setImageDrawable(resources.getDrawable(
                    if (it < 0)
                        R.drawable.ic_direction_down
                    else
                        R.drawable.ic_direction_up))
            directionHeader.setColorFilter(resources.getColor(
                    if (it < 0)
                        R.color.colorAccent
                    else
                        R.color.joinColor))
            percentDiffTextView.text = context!!.setRateDifference(percentDiff)
            percentDiffTextView.setTextColor(resources.getColor(
                    if (it < 0)
                        R.color.colorAccent
                    else
                        R.color.joinColor))
        }

        fiatDiffTextView.text = context!!.setQuantity(currentCurrency.currencySymbol, fiatDiff)
    }

    //todo [priority: low] rename flag
    private fun setHeaderBalances(flag: Boolean) {
        if (flag) {
            headerMainCurrency.text = context!!.setTokenBalance(TokenType.ETH.codeName, totalBalance)
            headerSecondaryCurrency.text = context!!.setBalanceWithCurrency(totalFiatBalance)
        } else {
            headerMainCurrency.text = context!!.setBalanceWithCurrency(totalFiatBalance)
            headerSecondaryCurrency.text = context!!.setTokenBalance(TokenType.ETH.codeName, totalBalance)
        }
    }

    interface MainFragmentListener {
        fun onMenuButtonClick()
        fun onFabClick()
        fun onTokenListItemClick(tokenType: TokenType)
        fun onTokenListItemSwipe(tokenType: TokenType, position: Int, direction: Int)
    }

    companion object {
        fun newInstance(): MainFragment {
            val fragment = MainFragment()
            return fragment
        }
    }
}