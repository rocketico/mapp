package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_statistics.*
import lecho.lib.hellocharts.model.*


class StatisticsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpCharts()
    }

    private fun setUpCharts() {
        //TODO DEBUG
        //Top chart
        val values = ArrayList<PointValue>()
        values.add(PointValue(0f, 2f))
        values.add(PointValue(1f, 4f))
        values.add(PointValue(2f, 3f))
        values.add(PointValue(3f, 6f))
        values.add(PointValue(4f, 3f))
        values.add(PointValue(5f, 8f))
        values.add(PointValue(6f, 6f))
        values.add(PointValue(7f, 6f))

        val line = Line(values)
        line.color = context!!.resources.getColor(R.color.colorPrimaryDark)
        line.isCubic = true
        line.strokeWidth = 1
        line.pointRadius = 3
        line.isFilled = true
        line.areaTransparency = 10

        val lines = ArrayList<Line>()
        lines.add(line)

        val data = LineChartData()
        data.lines = lines

        chart.isZoomEnabled = false
        chart.lineChartData = data

        //Bottom chart
        val columns = mutableListOf<Column>()
        val subColumns = mutableListOf<SubcolumnValue>()
        subColumns.add(SubcolumnValue(4f))
        subColumns.add(SubcolumnValue(7f))
        subColumns.add(SubcolumnValue(3f))
        subColumns.add(SubcolumnValue(1f))
        subColumns.add(SubcolumnValue(9f))

        columns.add(Column(subColumns))

        val columnChartData = ColumnChartData(columns)

        bottomChart.isZoomEnabled = false
        bottomChart.columnChartData = columnChartData
    }
}
