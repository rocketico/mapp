package io.rocketico.mapp.fragment

import android.annotation.SuppressLint
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
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenFlexibleItem
import io.rocketico.mapp.test.MainCurrencyEvent
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.header_main.*
import org.greenrobot.eventbus.EventBus
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.doAsyncResult
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.math.BigInteger

class MainFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var fragmentListener: MainFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    private lateinit var ethHelper: EthereumHelper

    private lateinit var wallet: Wallet
    private lateinit var currentCurrency: Currency

    private var totalBalance = 0f
    private var totalFiatBalance = 0f

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
        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet

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

    //todo implement getting info async in separated threads
    @SuppressLint("StringFormatMatches")
    private fun showTokens() {
        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            //updating balances and rates
            if (RateHelper.isOutdated(context!!, currentCurrency)) {
                val tmp = RateHelper.getTokenRateByDate()
                RateHelper.saveRates(context!!, RateHelper.RatesEntity.parse(tmp!!))
            }

            if (BalanceHelper.isTokenBalanceOutdated(context!!, TokenType.ETH)) {
                BalanceHelper.saveTokenBalance(context!!, TokenType.ETH, ethHelper.getBalance(wallet.address))
            }

            val badTokenList = mutableListOf<TokenType>()
            wallet.tokens?.forEach {token ->
                if (BalanceHelper.isTokenBalanceOutdated(context!!, token)) {
                    val result = doAsync({
                        badTokenList.add(token)
                        it.printStackTrace()
                    }) {
                        val updatedBalance = ethHelper.getBalanceErc20(
                                token.contractAddress,
                                wallet.address,
                                wallet.privateKey
                        )
                        BalanceHelper.saveTokenBalance(context!!, token, updatedBalance)
                    }

                    while (!result.isDone) {}
                }
            }

            badTokenList.forEach {
                wallet.tokens?.remove(it)
            }
            WalletManager(context!!).saveWallet(wallet)

            view?.context?.runOnUiThread {
                totalBalance = 0f
                totalFiatBalance = 0f

                val rates = RateHelper.loadRates(context!!, currentCurrency).rates

                //fill ether token
                val ethRate = rates.find { it.tokenType == TokenType.ETH }?.rate!!
                val ethBalance = BalanceHelper.loadTokenBalance(context!!, TokenType.ETH)!!
                val floatEthBalance = Utils.bigIntegerToFloat(ethBalance)

                totalBalance += floatEthBalance
                totalFiatBalance += floatEthBalance * ethRate

                listAdapter.addItem(TokenFlexibleItem(TokenType.ETH, currentCurrency, floatEthBalance, ethRate))

                //fill other tokens
                wallet.tokens?.forEach {
                    val tokenType = it

                    val tokenBalance = BalanceHelper.loadTokenBalance(context!!, tokenType)!!
                    val floatTokenBalance = Utils.bigIntegerToFloat(tokenBalance, tokenType.decimals)

                    val tokenRate = rates.find { it.tokenType ==  tokenType}?.rate!!

                    totalBalance += RateHelper.convertCurrency(tokenRate, ethRate, floatTokenBalance)
                    totalFiatBalance += floatTokenBalance * tokenRate

                    listAdapter.addItem(TokenFlexibleItem(tokenType, currentCurrency, floatTokenBalance, tokenRate))
                }

                setHeaderBalances(BalanceHelper.getMainCurrency(context!!))

                refresher.isRefreshing = false
            }
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

    @SuppressLint("StringFormatMatches")
    override fun onRefresh() {
        doAsync {
            val newRates = RateHelper.getTokenRateByDate()
            RateHelper.saveRates(context!!, RateHelper.RatesEntity.parse(newRates!!))

            BalanceHelper.saveTokenBalance(context!!, TokenType.ETH, ethHelper.getBalance(wallet.address))

            wallet.tokens?.forEach {
                BalanceHelper.saveTokenBalance(context!!, it, ethHelper.getBalanceErc20(
                        it.contractAddress,
                        wallet.address,
                        wallet.privateKey))
            }

            view?.context?.runOnUiThread {
                totalBalance = 0f
                totalFiatBalance = 0f

                val rates = RateHelper.loadRates(context!!, currentCurrency).rates
                val ethRate = rates.find { it.tokenType == TokenType.ETH }?.rate!!

                val newItems = listAdapter.currentItems
                newItems.forEach {
                    if (it is TokenFlexibleItem) {
                        val tokenType = it.tokenType

                        val tokenRate = rates.find { it.tokenType ==  tokenType}?.rate!!
                        val tokenBalance = BalanceHelper.loadTokenBalance(context!!, tokenType)!!
                        val floatTokenBalance = Utils.bigIntegerToFloat(tokenBalance, tokenType.decimals)

                        it.tokenRate = tokenRate
                        it.tokenBalance = floatTokenBalance

                        totalBalance += if (tokenType == TokenType.ETH) {
                            floatTokenBalance
                        } else {
                            RateHelper.convertCurrency(tokenRate, ethRate, floatTokenBalance)
                        }
                        totalFiatBalance += floatTokenBalance * tokenRate
                    }
                }

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

    @SuppressLint("StringFormatMatches")
    private fun setHeaderBalances(flag: Boolean) {
        if (flag) {
            tokensTotal.text = getString(R.string.balance_template, getString(R.string.ether_label),
                    totalBalance)
            fiatTotal.text = getString(R.string.balance_template, currentCurrency.currencySymbol,
                    totalFiatBalance)
        } else {
            tokensTotal.text = getString(R.string.balance_template, currentCurrency.currencySymbol,
                    totalFiatBalance)
            fiatTotal.text = getString(R.string.balance_template, getString(R.string.ether_label),
                    totalBalance)
        }
    }

    interface MainFragmentListener {
        fun onMenuButtonClick()
        fun onFabClick()
        fun onTokenListItemClick(tokenType: TokenType)
    }

    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet): MainFragment {
            val fragment = MainFragment()
            val args = Bundle()

            args.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = args

            return fragment
        }
    }
}