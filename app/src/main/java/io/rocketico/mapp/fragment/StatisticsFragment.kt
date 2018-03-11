package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.core.RateHelper
import io.rocketico.core.WalletManager
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_statistics.*
import lecho.lib.hellocharts.model.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread
import java.util.*


class StatisticsFragment : Fragment() {

    lateinit var walletManager: WalletManager
    lateinit var wallet: Wallet

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //todo move init wallet to fragment's constructor parameters
        walletManager = WalletManager(context!!)
        wallet = walletManager.getWallet()!!

        setUpCharts()
    }

    private fun setUpCharts() {
        val values = ArrayList<PointValue>()

        val subColumnsData = mutableListOf<SubcolumnValue>()

        doAsync({
            context?.runOnUiThread {
                toast(getString(R.string.update_info_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            val rates = RateHelper.getTokenRatesByRange(io.rocketico.mapp.Utils.yesterday(), Date())

            rates?.rates?.forEachIndexed { index, ratesItem ->
                var averageY = 0f
                ratesItem?.values?.forEach { rateItem ->
                    if (wallet.tokens!!.find { walletToken ->
                                (rateItem?.tokenSymbol == walletToken.type.codeName) || (rateItem?.tokenSymbol == TokenType.ETH.codeName)
                            } != null) {
                        averageY += rateItem?.rate!!
                    }
                }
                averageY /= ratesItem!!.values!!.size

                values.add(PointValue(index.toFloat(), averageY))

                subColumnsData.add(SubcolumnValue(ratesItem.volume!!));
            }

            uiThread {
                //Top chart
                val line = Line(values)
                line.color = context!!.resources.getColor(R.color.colorPrimaryDark)
                line.isCubic = true
                line.strokeWidth = 1
                line.pointRadius = 3
                line.isFilled = true
                line.areaTransparency = 10
                line.setHasLabelsOnlyForSelected(true)

                val lines = ArrayList<Line>()
                lines.add(line)

                val data = LineChartData()
                data.lines = lines

                chart.isZoomEnabled = false
                chart.lineChartData = data

                //Bottom chart
                val columns = mutableListOf<Column>()
                columns.add(Column(subColumnsData).setHasLabelsOnlyForSelected(true))

                val columnChartData = ColumnChartData(columns)

                bottomChart.isZoomEnabled = false
                bottomChart.columnChartData = columnChartData
            }

        }
    }
}
