package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.math.BigInteger

class HistoryFragment : Fragment() {

    lateinit var historyListAdapter: FlexibleAdapter<IFlexible<*>>
    lateinit var historyItems: MutableList<HistoryFlexibleItem>
    lateinit var ethereumHelper: EthereumHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ethereumHelper = EthereumHelper(Cc.ETH_NODE)
        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
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
            val history = ethereumHelper.getTokensHistory(listOf("", ""), Utils.nDaysAgo(10))
            val rates = RateHelper.getTokenRateByDate()

            uiThread {
                history?.forEach { historyItem ->
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
                                true,
                                currentTokenType.decimals
                        )
                    } else {
                        tmpItem.value = io.rocketico.core.Utils.bigIntegerToFloat(
                                BigInteger(historyItem.value, 16),
                                true,
                                18 // todo
                        )
                    }

                    tmpItem.fee = io.rocketico.core.Utils.bigIntegerToFloat(
                            BigInteger(historyItem.fee, 16),
                            true,
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
}