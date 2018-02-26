package io.rocketico.mapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_send_token.view.*
import kotlinx.android.synthetic.main.item_token.view.*
import java.util.*

class TokenSendFlexibleItem(listener: OnItemClickListener) : IFlexible<TokenSendFlexibleItem.ViewHolder> {
    override fun getItemViewType() = 0

    private lateinit var view: View
    private var position: Int = -1

    override fun onViewDetached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun onViewAttached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun getBubbleText(position: Int) = ""

    private var onItemClickListener: OnItemClickListener = listener

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

    override fun getLayoutRes() = R.layout.item_send_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<*>): ViewHolder {
        return ViewHolder(view)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int, payloads: List<*>) {
        //TODO debug
        fun rnd() = "%.4f".format(Random().nextFloat())
        holder.tokenName.text = rnd()
        holder.tokenBalance.text = rnd()
        holder.tokenFiatBalance.text = rnd()

        holder.view.setOnClickListener {
            onItemClickListener.onClick(position)
        }
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int) {

    }

    interface OnItemClickListener {
        fun onClick(position: Int)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val tokenImage: CircleImageView? = view.tokenSendImage
        val tokenName: TextView = view.tokenSendName
        val tokenBalance: TextView = view.tokenSendBalance
        val tokenFiatBalance: TextView = view.tokenSendFiatBalance
    }
}