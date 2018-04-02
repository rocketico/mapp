package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.Utils
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.setBalanceWithCurrency
import io.rocketico.mapp.setEthBalance
import kotlinx.android.synthetic.main.item_history.view.*
import java.util.*

data class HistoryFlexibleItem(val item: HistoryItem) : AbstractFlexibleItem<HistoryFlexibleItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_history

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (item.isReceived) {
            holder.direction.setImageDrawable(holder.itemView.resources.getDrawable(R.drawable.ic_direction_down))
        } else {
            holder.direction.setImageDrawable(holder.itemView.resources.getDrawable(R.drawable.ic_direction_up))
        }
        val context = holder.itemView.context

        holder.address.text = item.address!!.substring(0..15) + "..."
        holder.valueFiat.text = context.setBalanceWithCurrency(item.valueFiat)
        holder.value.text = item.tokenName + " " + item.value.toString()
        holder.fee.text = Utils.scaleFloat(item.feeFiat!!) + " " + item.tokenName
        holder.feeFiat.text = context.setBalanceWithCurrency(item.fee)
        holder.confirmations.text = item.confirmations.toString()
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val direction: ImageView = view.direction
        val address: TextView = view.address
        val valueFiat: TextView = view.valueFiat
        val value: TextView = view.value
        val fee: TextView = view.fee
        val feeFiat: TextView = view.feeFiat
        val confirmations: TextView = view.confirmations
        val date: TextView = view.date
    }

    class HistoryItem(
            var tokenName: String? = null,
            var isReceived: Boolean = false,
            var address: String? = null,
            var valueFiat: Float? = null,
            var value: Float? = null,
            var fee: Float? = null,
            var feeFiat: Float? = null,
            var confirmations: Long? = null,
            var date: Date? = null
    )
}