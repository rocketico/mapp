package io.rocketico.mapp.fragment

import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.EthereumHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.adapter.HistoryFlexibleItem
import io.rocketico.mapp.event.RefreshEvent
import io.rocketico.mapp.loadData
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.include_date_panel.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast

class HistoryFragment : Fragment() {

    lateinit var historyListAdapter: FlexibleAdapter<IFlexible<*>>
    lateinit var historyItems: MutableList<HistoryFlexibleItem>
    lateinit var ethereumHelper: EthereumHelper
    private var currentDirection: TokenDirection = TokenDirection.ALL
    private var currentDayRange: Int = 1
    private lateinit var wallet: Wallet

    private var tokenType: TokenType? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)

        arguments?.getSerializable(TOKEN_TYPE)?.let { tokenType = it as TokenType }
        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet

        ethereumHelper = EthereumHelper(Cc.ETH_NODE)

        showHistory()
        setupButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()

        EventBus.getDefault().unregister(this)
    }

    private fun setupButtons() {
        button1d.setOnClickListener { currentDayRange = 1; showHistory(); selectButtonRange(button1d) }
        button1w.setOnClickListener { currentDayRange = 7; showHistory(); selectButtonRange(button1w) }
        button1m.setOnClickListener { currentDayRange = 30; showHistory(); selectButtonRange(button1m) }
        button3m.setOnClickListener { currentDayRange = 30 * 3; showHistory(); selectButtonRange(button3m) }
        button6m.setOnClickListener { currentDayRange = 30 * 6; showHistory(); selectButtonRange(button6m) }
        button1y.setOnClickListener { currentDayRange = 30 * 12; showHistory(); selectButtonRange(button1y) }
        button2y.setOnClickListener { currentDayRange = 30 * 12 * 2; showHistory(); selectButtonRange(button2y) }

        sent.setOnClickListener { currentDirection = TokenDirection.OUT; showHistory(); selectButtonDirection(sent) }
        all.setOnClickListener { currentDirection = TokenDirection.ALL; showHistory(); selectButtonDirection(all) }
        receive.setOnClickListener { currentDirection = TokenDirection.IN; showHistory(); selectButtonDirection(receive) }
    }

    private fun selectButtonDirection(button: TextView) {
        sent.typeface = Typeface.DEFAULT
        all.typeface = Typeface.DEFAULT
        receive.typeface = Typeface.DEFAULT

        button.typeface = Typeface.DEFAULT_BOLD
    }

    private fun selectButtonRange(button: TextView) {
        button1d.typeface = Typeface.DEFAULT
        button1w.typeface = Typeface.DEFAULT
        button1m.typeface = Typeface.DEFAULT
        button3m.typeface = Typeface.DEFAULT
        button6m.typeface = Typeface.DEFAULT
        button1y.typeface = Typeface.DEFAULT
        button2y.typeface = Typeface.DEFAULT

        button.typeface = Typeface.DEFAULT_BOLD
    }

    private fun showHistory() {
        historyItems = mutableListOf()
        historyListAdapter = FlexibleAdapter(historyItems as List<IFlexible<*>>)
        recyclerViewHistory.layoutManager = LinearLayoutManager(context)
        recyclerViewHistory.adapter = historyListAdapter

        noHistoryLabel.visibility = View.GONE
        progressBar.visibility = View.VISIBLE

        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            //todo implement choosing days count
            val typeList = mutableListOf<String>()

            if (tokenType == null) {
                typeList.add(TokenType.ETH.codeName)
                typeList.addAll(wallet.tokens?.map { it.codeName }!!)
            } else {
                typeList.add(tokenType?.codeName!!)
            }

            val history = loadData {
                ethereumHelper.getTokensHistory(wallet.address, typeList, Utils.nDaysAgo(currentDayRange))
            }?.sortedByDescending { it.date }

            val tokenList = mutableListOf(TokenType.ETH.codeName)
            tokenList.addAll(wallet.tokens?.map { it.codeName }!!)

            val rates = loadData { RateHelper.getTokenRateByDate(tokenList) }

            view?.context?.runOnUiThread {
                progressBar.visibility = View.GONE

                history?.forEach { historyItem ->
                    if (currentDirection != TokenDirection.ALL) {
                        if (currentDirection == TokenDirection.IN) {
                            if (!historyItem.received) return@forEach
                        } else if (currentDirection == TokenDirection.OUT) {
                            if (historyItem.received) return@forEach
                        }
                    }
                    val tmpItem = HistoryFlexibleItem.HistoryItem()
                    tmpItem.isReceived = historyItem.received
                    tmpItem.address = historyItem.addressFrom
                    tmpItem.date = historyItem.date
                    tmpItem.confirmations = historyItem.confirmations
                    tmpItem.tokenName = historyItem.tokenType!!

                    tmpItem.value = historyItem.value
                    tmpItem.fee = historyItem.fee


                    val fiatRate = rates?.rates?.find { rate -> rate.tokenSymbol == historyItem.tokenType }?.rate

                    fiatRate?.let {
                        tmpItem.valueFiat = tmpItem.value!! * fiatRate
                        tmpItem.feeFiat = tmpItem.fee!! / fiatRate
                    }

                    historyListAdapter.addItem(HistoryFlexibleItem(context!!, tmpItem))
                }

                if (historyListAdapter.itemCount == 0) {
                    noHistoryLabel.visibility = View.VISIBLE
                } else {
                    noHistoryLabel.visibility = View.GONE
                }
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {
        showHistory()
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"
        private const val WALLET_KEY = "wallet_key"

        private enum class TokenDirection {
            OUT,
            IN,
            ALL
        }

        fun newInstance(wallet: Wallet, tokenType: TokenType? = null): HistoryFragment {
            val fragment = HistoryFragment()
            val bundle = Bundle()
            tokenType?.let { bundle.putSerializable(TOKEN_TYPE, it) }
            bundle.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }
}