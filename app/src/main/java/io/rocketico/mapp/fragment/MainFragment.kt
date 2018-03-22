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
import kotlinx.android.synthetic.main.bottom_main.*
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.fragment_main.*
import kotlinx.android.synthetic.main.header_main.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class MainFragment : Fragment() {

    private lateinit var fragmentListener: MainFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    private lateinit var walletManager: WalletManager
    private lateinit var wallet: Wallet

    private lateinit var ethHelper: EthereumHelper

    private lateinit var currentCurrency: Currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as MainFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        prograssBar.visibility = View.VISIBLE

        walletManager = WalletManager(context!!)
        wallet = walletManager.getWallet()!!

        ethHelper = EthereumHelper(Cc.ETH_NODE)

        viewPager.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when(position) {
                0 -> StatisticsFragment.newInstance()
                1 -> HistoryFragment()
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

            wallet.tokens?.forEach {
                if (BalanceHelper.isTokenBalanceOutdated(context!!, it)) {
                    BalanceHelper.saveTokenBalance(context!!, it, ethHelper.getBalanceErc20(
                            it.contractAddress,
                            wallet.address,
                            wallet.privateKey
                    ))
                }
            }

            uiThread {
                var totalBalance = 0f
                var totalFiatBalance = 0f

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

                fiatCurrency.text = currentCurrency.currencySymbol
                tokensTotal.text = totalBalance.toString()
                fiatTotal.text = Utils.scaleFloat(totalFiatBalance)

                prograssBar.visibility = View.GONE
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
    }

    interface MainFragmentListener {
        fun onMenuButtonClick()
        fun onFabClick()
        fun onTokenListItemClick(tokenType: TokenType)
    }

    companion object {
        fun newInstance(): MainFragment {
            return MainFragment()
        }
    }
}