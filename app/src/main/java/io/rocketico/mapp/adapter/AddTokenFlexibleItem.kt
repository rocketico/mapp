package io.rocketico.mapp.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_add_token.view.*

class AddTokenFlexibleItem(private val context: Context, private val tokenType: TokenType,
                           listener: OnItemClickListener) : IFlexible<AddTokenFlexibleItem.ViewHolder> {

    override fun getItemViewType() = 0

    private lateinit var view: View
    private var position: Int = -1

    override fun onViewDetached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun onViewAttached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun getBubbleText(position: Int) = ""

    var onItemClickListener: OnItemClickListener = listener

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

    override fun getLayoutRes() = R.layout.item_add_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<*>): ViewHolder {
        return ViewHolder(view)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int, payloads: List<*>) {
        //TODO debug
        holder.tokenName.text = tokenType.toString()
        holder.tokenCoast.text = "0.01"
        holder.tokenMarket.text = "Bitrix"

        holder.view.setOnClickListener {
            onItemClickListener.onAddTokenListItemClick(tokenType)
        }
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<*>, holder: ViewHolder, position: Int) {

    }

    interface OnItemClickListener {
        fun onAddTokenListItemClick(tokenType: TokenType)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val tokenImage: CircleImageView? = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenCoast: TextView = view.tokenCoast
        val tokenMarket: TextView = view.tokenMarket
    }
}