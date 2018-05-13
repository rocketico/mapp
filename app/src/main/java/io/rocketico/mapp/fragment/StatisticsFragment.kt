package io.rocketico.mapp.fragment

import android.content.Context
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.utils.MPPointF
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.core.model.response.TokenRatesRangeResponse
import io.rocketico.mapp.R
import io.rocketico.mapp.event.RefreshEvent
import io.rocketico.mapp.loadData
import io.rocketico.mapp.setBalanceWithCurrency
import kotlinx.android.synthetic.main.fragment_statistics.*
import kotlinx.android.synthetic.main.include_date_panel.*
import kotlinx.android.synthetic.main.view_marker.view.*
import lecho.lib.hellocharts.model.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.util.*

class StatisticsFragment : Fragment() {

    lateinit var wallet: Wallet
    private var token: TokenType? = null
    private lateinit var currentCurrency: Currency

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        EventBus.getDefault().register(this)

        currentCurrency = RateHelper.getCurrentCurrency(context!!)

        arguments?.getSerializable(TOKEN_KEY)?.let { token = it as TokenType }

        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet

        stylingTopChart()
        showCharts()
        setupButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        EventBus.getDefault().unregister(this)
    }

    private fun setupButtons() {
        all.visibility = View.GONE
        receive.visibility = View.GONE
        sent.visibility = View.GONE

        button1d.setOnClickListener { showCharts(1); selectButton(button1d) }
        button1w.setOnClickListener { showCharts(7); selectButton(button1w) }
        button1m.setOnClickListener { showCharts(30); selectButton(button1m) }
        button3m.setOnClickListener { showCharts(30 * 3); selectButton(button3m) }
        button6m.setOnClickListener { showCharts(30 * 6); selectButton(button6m) }
        button1y.setOnClickListener { showCharts(30 * 12); selectButton(button1y) }
        button2y.setOnClickListener { showCharts(30 * 12 * 2); selectButton(button2y) }
    }

    private fun selectButton(button: TextView) {
        button1d.typeface = Typeface.DEFAULT
        button1w.typeface = Typeface.DEFAULT
        button1m.typeface = Typeface.DEFAULT
        button3m.typeface = Typeface.DEFAULT
        button6m.typeface = Typeface.DEFAULT
        button1y.typeface = Typeface.DEFAULT
        button2y.typeface = Typeface.DEFAULT

        button.typeface = Typeface.DEFAULT_BOLD
    }

    private fun stylingTopChart() {
        chart.setViewPortOffsets(-10f, 0f,-10f,0f)
        chart.description.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = false
        chart.setScaleEnabled(false)
        chart.setDrawGridBackground(false)
        chart.xAxis.isEnabled = false
        chart.axisLeft.isEnabled = false
        chart.axisRight.isEnabled = false
        chart.legend.isEnabled = false

        val marker = RateMarker(context!!, R.layout.view_marker)
        marker.chartView = chart
        chart.marker = marker
    }

    private fun showCharts(nDaysAgo: Int = 1) {
        bottomChart.columnChartData = null
        bodyStatistics.visibility = View.GONE
        noStatistics.visibility = View.GONE

        val values = mutableListOf<Entry>()
        val subColumnsData = mutableListOf<SubcolumnValue>()

        progressBar.visibility = View.VISIBLE
        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            val tokenList = mutableListOf(TokenType.ETH.codeName)

            if (token != null) {
                tokenList.add(token?.codeName!!)
            }

            val ratesResponse = loadData { RateHelper.getTokenRatesByRange(tokenList, io.rocketico.mapp.Utils.nDaysAgo(nDaysAgo)) }
            val rates = ratesResponse?.rates

            var ethTopValue = Float.MIN_VALUE;
            var ethBottomValue = Float.MAX_VALUE;

            rates?.forEachIndexed { index, ratesItem ->
                var averageYInEther = 0f
                var averageVolume = 0f
                var foundTokensCount = 0
                val ethRate = ratesItem?.values?.find { it?.tokenSymbol == TokenType.ETH.codeName }?.rate
                ratesItem?.values?.forEach { rateItem ->
                    if (rateItem?.tokenSymbol == TokenType.ETH.codeName) {
                        if (token == null || token == TokenType.ETH) {
                            averageYInEther += rateItem.rate!!
                            averageVolume += rateItem.volume!!
                            foundTokensCount++
                        }
                    } else {
                        if (token != null) {
                            val walletToken = wallet.tokens!!.find { walletToken ->
                                (rateItem?.tokenSymbol?.toLowerCase() == walletToken.codeName.toLowerCase())
                            } ?: return@forEach

                            val tokenRate = token?.let { rateItem?.rate!! }
                                    ?: (rateItem?.rate!! / ethRate!!)

                            averageYInEther += tokenRate
                            averageVolume += rateItem?.volume!!
                            foundTokensCount++
                        }
                    }
                }
                averageYInEther /= foundTokensCount
                averageVolume /= foundTokensCount

                if (averageYInEther > ethTopValue) {
                    ethTopValue = averageYInEther
                } else {
                    if (averageYInEther <= ethBottomValue) {
                        ethBottomValue = averageYInEther
                    }
                }

                values.add(Entry(index.toFloat(), averageYInEther))
                subColumnsData.add(SubcolumnValue(averageVolume))
            }

            view?.context?.runOnUiThread {
                progressBar.visibility = View.GONE
                if (rates == null || rates.isEmpty()) {
                    noStatistics.visibility = View.VISIBLE
                } else {
                    bodyStatistics.visibility = View.VISIBLE
                }
                topValue.text = Utils.round(ethTopValue, 5).toString()
                bottomValue.text = Utils.round(ethBottomValue, 5).toString()

                //Top chart
                val dataSet = LineDataSet(values, "Rate")
                dataSet.mode = LineDataSet.Mode.CUBIC_BEZIER
                dataSet.lineWidth = 1f
                dataSet.setDrawValues(false)
                dataSet.circleRadius = 3f
                dataSet.color = context!!.resources.getColor(R.color.colorPrimaryDark)
                dataSet.setCircleColor(context!!.resources.getColor(R.color.colorPrimaryDark))
                dataSet.setCircleColorHole(context!!.resources.getColor(R.color.colorPrimaryDark))
                dataSet.fillDrawable = getDrawable(R.drawable.statistics_gradient)
                dataSet.setDrawFilled(true)
                dataSet.setDrawVerticalHighlightIndicator(false)
                dataSet.setDrawHorizontalHighlightIndicator(false)

                val lineData = LineData(dataSet)

                chart.data = lineData
                chart.invalidate()

                //Bottom chart
                val columns = mutableListOf<Column>()
                columns.add(Column(subColumnsData).setHasLabelsOnlyForSelected(true))

                val columnChartData = ColumnChartData(columns)

                bottomChart.isZoomEnabled = false
                bottomChart.columnChartData = columnChartData
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: RefreshEvent) {
        showCharts()
    }

    private class RateMarker(context: Context, res: Int)
        : MarkerView(context, res) {
        override fun getOffset(): MPPointF {
            return MPPointF(-(width / 2f), (-height * 1.2f))
        }

        override fun refreshContent(e: Entry, highlight: Highlight) {
            markerText.text = context.setBalanceWithCurrency(e.y, 2)
            super.refreshContent(e, highlight)
        }
    }

    companion object {
        private const val TOKEN_KEY = "token_key"
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet, token: TokenType? = null): StatisticsFragment {
            val bundle = Bundle()
            token?.let {
                bundle.putSerializable(TOKEN_KEY, token)
            }
            bundle.putSerializable(WALLET_KEY, wallet)
            val result = StatisticsFragment()
            result.arguments = bundle
            return result
        }
    }
}
