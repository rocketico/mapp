package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import io.rocketico.core.model.response.TokenInfoResponse
import io.rocketico.mapp.R
import io.rocketico.mapp.setBalanceWithCurrency
import kotlinx.android.synthetic.main.item_markets.view.*

class ExpandableListAdapter(val context: Context, marketList: List<TokenInfoResponse.TokenInfoFromMarket>) : BaseExpandableListAdapter() {

    private var mainMarket = marketList[0]
    private var secondaryMarkets = mutableListOf<TokenInfoResponse.TokenInfoFromMarket>()
    private val inflater = LayoutInflater.from(context)

    init {
        for (i in 1 until marketList.size) {
            secondaryMarkets.add(marketList[i])
        }
    }

    override fun getGroup(groupPosition: Int): Any = mainMarket

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean = true

    override fun hasStableIds(): Boolean = false

    @SuppressLint("StringFormatMatches")
    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.item_markets, null)
        view.marketName.text = mainMarket.marketName
        view.exchange.text = context.setBalanceWithCurrency(mainMarket.exchange)
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

    @SuppressLint("StringFormatMatches")
    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.item_markets, null)
        view.marketName.text = secondaryMarkets[childPosition].marketName
        view.exchange.text = context.setBalanceWithCurrency(secondaryMarkets[childPosition].exchange)
        return view
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getGroupCount(): Int = 1

}
