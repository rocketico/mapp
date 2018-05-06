package io.rocketico.mapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_fund.view.*
import java.util.*

data class FundFlexibleItem(val fundName: String,
                            val progress: Int,
                            val balance: Int,
                            val participants: Int) :
        AbstractFlexibleItem<FundFlexibleItem.FundViewHolder>(),
        IFilterable<String> {

    override fun filter(constraint: String): Boolean {
        if (fundName.startsWith(constraint, true)) return true
        return false
    }

    override fun getLayoutRes(): Int = R.layout.item_fund

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): FundViewHolder {
        return FundViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: FundViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.fundName.text = fundName
        holder.progressText.text = "ETH 500 / " + progress + "%"
        holder.progressBar.max = 100
        holder.progressBar.progress = if (progress > 100) 100 else if (progress < 0) 0 else progress
        holder.balance.text = "ETH " + balance
        holder.status.text = "500 SHPING"
        holder.endDate.text = "26 feb"
        holder.participants.text = participants.toString()
    }

    class FundViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val fundName: TextView = view.fundName
        val progressText: TextView = view.progressText
        val progressBar: ProgressBar = view.progress
        val balance: TextView = view.balanceText
        val status: TextView = view.statusText
        val endDate: TextView = view.dateText
        val participants: TextView = view.participantsText
    }
}