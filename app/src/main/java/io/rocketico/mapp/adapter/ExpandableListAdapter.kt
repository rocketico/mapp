package io.rocketico.mapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_markets.view.*

class ExpandableListAdapter(context: Context, marketList: List<String>) : BaseExpandableListAdapter() {

    private var mainMarket = marketList[0]
    private var secondaryMarkets = mutableListOf<String>()
    private val inflater = LayoutInflater.from(context)

    init {
        for (i in 1 until marketList.size) {
            secondaryMarkets.add(marketList[i])
        }
    }

    override fun getGroup(groupPosition: Int): Any = mainMarket

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.item_markets, null)
        view.marketName.text = mainMarket
        view.arrow.visibility = View.VISIBLE

        if (isExpanded) {
            view.arrow.setImageResource(R.drawable.ic_expand_less)
        } else {
            view.arrow.setImageResource(R.drawable.ic_expand_more)
        }

        return view
    }

    override fun getChildrenCount(groupPosition: Int): Int = secondaryMarkets.size

    override fun getChild(groupPosition: Int, childPosition: Int): Any = secondaryMarkets[childPosition]

    override fun getGroupId(groupPosition: Int): Long = 0L

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.item_markets, null)
        view.marketName.text = secondaryMarkets[childPosition]
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = 1

}
