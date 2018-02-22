package io.rocketico.mapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_token.view.*
import java.util.*

class TokenFlexibleItem : IFlexible<TokenFlexibleItem.ViewHolder> {
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

    override fun getLayoutRes() = R.layout.item_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<*>): ViewHolder {
        return ViewHolder(view)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int, payloads: List<*>) {
        //TODO debug
        fun rnd() = "%.4f".format(Random().nextFloat())
        holder.tokenName.text = rnd()
        holder.tokenRate.text = rnd()
        holder.tokenRateDiff.text = rnd()
        holder.tokenBalance.text = rnd()
        holder.tokenCurrencyBalance.text = rnd()
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int) {

    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val tokenImage: CircleImageView? = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenRate: TextView = view.tokenRate
        val tokenRateDiff: TextView = view.tokenRateDiff
        val tokenBalance: TextView = view.tokenBalance
        val tokenCurrencyBalance: TextView = view.tokenCurrencyBalance
    }
}