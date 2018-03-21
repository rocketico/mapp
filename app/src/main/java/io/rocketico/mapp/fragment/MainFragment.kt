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

    private lateinit var mainFragmentListener: MainFragmentListener
    private lateinit var tokenListAdapter: FlexibleAdapter<IFlexible<*>>
    private lateinit var tokens: MutableList<TokenFlexibleItem>

    private lateinit var walletManager: WalletManager
    private lateinit var wallet: Wallet

    private lateinit var ethHelper: EthereumHelper

    private lateinit var currentCurrency: Currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mainFragmentListener = activity as MainFragmentListener
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

        setupListeners()
        setupRecyclerViews()
    }

    private fun setupRecyclerViews() {
        tokens = mutableListOf()
        tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
        tokenList.layoutManager = LinearLayoutManager(context)
        tokenList.adapter = tokenListAdapter

        showTokens()
    }

    private fun showTokens() {
        val itemListener = activity as TokenFlexibleItem.OnItemClickListener

        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            var totalBalance = 0f
            var totalFiatBalance = 0f

            if (RateHelper.isOutdated(context!!, currentCurrency)) {
                val tmp = RateHelper.getTokenRateByDate()
                RateHelper.saveRates(context!!, RateHelper.RatesEntity.parse(tmp!!))
            }

            val rates = RateHelper.loadRates(context!!, currentCurrency).rates

            //fill ether token
            val ethRate = rates.find { it.tokenType.codeName == TokenType.ETH.codeName }?.rate

            if (BalanceHelper.isTokenBalanceOutdated(context!!, TokenType.ETH)) {
                BalanceHelper.saveTokenBalance(context!!, TokenType.ETH, ethHelper.getBalance(wallet.address))
            }
            val ethBalance = BalanceHelper.loadTokenBalance(context!!, TokenType.ETH)!!

            val floatEthBalance = Utils.bigIntegerToFloat(ethBalance)


            totalBalance += floatEthBalance
            if (ethRate != null) {
                totalFiatBalance += floatEthBalance * ethRate
            }

            //fill other tokens
            wallet.tokens?.forEach {
                if (it.isEther()) return@forEach // skip ether token

                val tokenType = it

                if (BalanceHelper.isTokenBalanceOutdated(context!!, tokenType)) {
                    BalanceHelper.saveTokenBalance(context!!, tokenType, ethHelper.getBalanceErc20(
                            tokenType.contractAddress,
                            wallet.address,
                            wallet.privateKey
                    ))
                }

                val tokenBalance = BalanceHelper.loadTokenBalance(context!!, tokenType)!!

                val floatTokenBalance = Utils.bigIntegerToFloat(tokenBalance, tokenType.decimals)
                val tokenRate = rates.find { it.tokenType ==  tokenType}?.rate

                BalanceHelper.saveTokenBalance(context!!, tokenType, tokenBalance)

                totalBalance += RateHelper.convertCurrency(tokenRate!!, ethRate!!, floatTokenBalance)
                totalFiatBalance += floatTokenBalance * tokenRate
            }
            uiThread {
                tokenListAdapter.addItem(TokenFlexibleItem(context!!, TokenType.ETH, itemListener))

                wallet.tokens?.forEach {
                    if (it.isEther()) return@forEach // skip ether token
                    tokenListAdapter.addItem(TokenFlexibleItem(context!!, it, itemListener))
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
                mainFragmentListener.onMenuButtonClick()
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
                mainFragmentListener.onFabClick()
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