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
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.adapter.HistoryFlexibleItem
import kotlinx.android.synthetic.main.fragment_history.*
import kotlinx.android.synthetic.main.include_date_panel.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.math.BigInteger

class HistoryFragment : Fragment() {

    lateinit var historyListAdapter: FlexibleAdapter<IFlexible<*>>
    lateinit var historyItems: MutableList<HistoryFlexibleItem>
    lateinit var ethereumHelper: EthereumHelper
    private var currentDirection: TokenDirection = TokenDirection.ALL
    private var currentDayRange: Int = 1

    private var tokenType: TokenType? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.getSerializable(TOKEN_TYPE)?.let { tokenType = it as TokenType }

        ethereumHelper = EthereumHelper(Cc.ETH_NODE)
        showHistory()
        setupButtons()
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

        //todo implement loading animation
        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            //todo implement choosing days count
            val history = ethereumHelper.getTokensHistory(listOf("", ""), Utils.nDaysAgo(currentDayRange))
            val rates = RateHelper.getTokenRateByDate()

            uiThread {
                history?.forEach { historyItem ->
                    if (currentDirection != TokenDirection.ALL) {
                        if (currentDirection == TokenDirection.IN) {
                            if (!historyItem.isReceived) return@forEach
                        } else if (currentDirection == TokenDirection.OUT) {
                            if (historyItem.isReceived) return@forEach
                        }
                    }
                    val tmpItem = HistoryFlexibleItem.HistoryItem()
                    tmpItem.isReceived = historyItem.isReceived
                    tmpItem.address = historyItem.address
                    tmpItem.date = historyItem.date
                    tmpItem.confirmations = historyItem.confirmations
                    tmpItem.tokenName = historyItem.tokenType!!

                    val currentTokenType = TokenType.values().find { tt -> tt.codeName.toLowerCase() == historyItem.tokenType!!.toLowerCase() }
                    if (currentTokenType != null) {
                        tmpItem.value = io.rocketico.core.Utils.bigIntegerToFloat(
                                BigInteger(historyItem.value, 16),
                                currentTokenType.decimals
                        )
                    } else {
                        tmpItem.value = io.rocketico.core.Utils.bigIntegerToFloat(
                                BigInteger(historyItem.value, 16),
                                18 // todo
                        )
                    }

                    tmpItem.fee = io.rocketico.core.Utils.bigIntegerToFloat(
                            BigInteger(historyItem.fee, 16),
                            18
                    )


                    val fiatRate = rates?.rates?.find { rate -> rate.tokenSymbol == historyItem.tokenType }?.rate

                    fiatRate?.let {
                        tmpItem.valueFiat = tmpItem.value!! * fiatRate
                        tmpItem.feeFiat = tmpItem.fee!! * fiatRate
                    }

                    historyListAdapter.addItem(HistoryFlexibleItem(tmpItem))
                }
            }
        }
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"

        private enum class TokenDirection {
            OUT,
            IN,
            ALL
        }

        fun newInstance(tokenType: TokenType? = null): HistoryFragment {
            val fragment = HistoryFragment()
            val bundle = Bundle()
            tokenType?.let { bundle.putSerializable(TOKEN_TYPE, it) }
            fragment.arguments = bundle
            return fragment
        }
    }
}