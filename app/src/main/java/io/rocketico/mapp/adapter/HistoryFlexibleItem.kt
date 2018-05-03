package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.text.format.DateUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.setBalanceWithCurrency
import io.rocketico.mapp.setTokenBalance
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
        holder.valueFiat.text = context.setBalanceWithCurrency(item.valueFiat, 2)
        holder.value.text = context!!.setTokenBalance(item.tokenName!!, item.value, 5)
        holder.fee.text = context!!.setTokenBalance(TokenType.ETH.codeName, item.fee, 5)
        holder.feeFiat.text = context.setBalanceWithCurrency(item.fee, 2)
        if (item.date?.time == null) {
            holder.date.text = context.getString(R.string.unknown)
        } else {
            holder.date.text =
                    DateUtils.getRelativeTimeSpanString(
                            item.date!!.time,
                            System.currentTimeMillis(),
                            DateUtils.MINUTE_IN_MILLIS
                    )
        }

        val confirmations: Int
        if (item.confirmations!! > 12L) {
            confirmations = 12
        } else {
            confirmations = item.confirmations!!.toInt()
        }

        holder.confirmations.text = "${context.getString(R.string.status)} $confirmations/12";
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