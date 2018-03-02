package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_statistics.*
import lecho.lib.hellocharts.model.*
import java.util.*


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
        values.add(PointValue(0f, Random().nextInt(15).toFloat()))
        values.add(PointValue(1f, Random().nextInt(15).toFloat()))
        values.add(PointValue(2f, Random().nextInt(15).toFloat()))
        values.add(PointValue(3f, Random().nextInt(15).toFloat()))
        values.add(PointValue(4f, Random().nextInt(15).toFloat()))
        values.add(PointValue(5f, Random().nextInt(15).toFloat()))
        values.add(PointValue(6f, Random().nextInt(15).toFloat()))
        values.add(PointValue(7f, Random().nextInt(15).toFloat()))

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
        val subColumnsData1 = mutableListOf<SubcolumnValue>()
        subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData1.add(SubcolumnValue(Random().nextInt(15).toFloat()));

        val subColumnsData2 = mutableListOf<SubcolumnValue>()
        subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData2.add(SubcolumnValue(Random().nextInt(15).toFloat()));

        val subColumnsData3 = mutableListOf<SubcolumnValue>()
        subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData3.add(SubcolumnValue(Random().nextInt(15).toFloat()));

        val subColumnsData4 = mutableListOf<SubcolumnValue>()
        subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData4.add(SubcolumnValue(Random().nextInt(15).toFloat()));

        val subColumnsData5 = mutableListOf<SubcolumnValue>()
        subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));
        subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));subColumnsData5.add(SubcolumnValue(Random().nextInt(15).toFloat()));

        columns.add(Column(subColumnsData1).setHasLabelsOnlyForSelected(true))
        columns.add(Column(subColumnsData2).setHasLabelsOnlyForSelected(true))
        columns.add(Column(subColumnsData3).setHasLabelsOnlyForSelected(true))
        columns.add(Column(subColumnsData4).setHasLabelsOnlyForSelected(true))
        columns.add(Column(subColumnsData5).setHasLabelsOnlyForSelected(true))

        val columnChartData = ColumnChartData(columns)

        bottomChart.isZoomEnabled = false
        bottomChart.columnChartData = columnChartData
    }
}
