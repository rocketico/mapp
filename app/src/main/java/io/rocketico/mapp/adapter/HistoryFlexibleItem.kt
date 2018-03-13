package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_history.view.*
import java.util.*

class HistoryFlexibleItem(val item: HistoryItem) : IFlexible<HistoryFlexibleItem.ViewHolder> {
    override fun getItemViewType() = 0

    private lateinit var view: View
    private var position: Int = -1

    override fun onViewDetached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun onViewAttached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun getBubbleText(position: Int) = ""

    var onItemClickListener: OnItemClickListener? = null

    override fun isEnabled(): Boolean {
        return true
    }

    override fun setEnabled(enabled: Boolean) {

    }

    override fun isHidden(): Boolean {
        return false
    }

    override fun setHidden(hidden: Boolean) {

    }

    override fun getSpanSize(spanCount: Int, position: Int): Int {
        return 1
    }

    override fun shouldNotifyChange(newItem: IFlexible<*>): Boolean {
        return false
    }

    override fun isSelectable(): Boolean {
        return false
    }

    override fun setSelectable(selectable: Boolean) {

    }

    override fun isDraggable() = false

    override fun setDraggable(draggable: Boolean) {

    }

    override fun isSwipeable() = false


    override fun setSwipeable(swipeable: Boolean) {

    }

    override fun getLayoutRes() = R.layout.item_history

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<*>): ViewHolder {
        return ViewHolder(view)
    }

    @SuppressLint("SetTextI18n")
    override fun bindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int, payloads: List<*>) {
        if (item.isReceived) {
            holder.direction.setImageDrawable(holder.view.resources.getDrawable(R.drawable.ic_direction_down))
        } else {
            holder.direction.setImageDrawable(holder.view.resources.getDrawable(R.drawable.ic_direction_up))
        }
        holder.address.text = item.address!!.substring(0..15) + "..."
        holder.valueFiat.text = item.value?.toString()
        holder.value.text = item.value.toString()
        holder.fee.text = item.fee.toString() + " " + item.tokenName
        holder.feeFiat.text = item.feeFiat?.toString()
        holder.confirmations.text = item.confirmations.toString()
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int) {

    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
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