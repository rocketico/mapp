package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_token.view.*

class TokenFlexibleItem(private val context: Context, private val tokenType: TokenType,
                        listener: OnItemClickListener) : IFlexible<TokenFlexibleItem.ViewHolder> {

    override fun getItemViewType() = 0

    private lateinit var view: View
    private var position: Int = -1

    override fun onViewDetached(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?, position: Int) {

    }

    override fun onViewAttached(adapter: FlexibleAdapter<IFlexible<*>>?, holder: ViewHolder?, position: Int) {

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

    override fun getLayoutRes() = R.layout.item_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view)
    }

    @SuppressLint("StringFormatMatches")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        //TODO check rate
        val currentCurrency = RateHelper.getCurrentCurrency(context)
        val rate = RateHelper.getTokenRate(context,tokenType, currentCurrency)?.rate!!
        val balance = Utils.bigIntegerToFloat(BalanceHelper.loadTokenBalance(context, tokenType)!!)
        
        holder.tokenName.text = tokenType.toString()
        holder.tokenRate.text = context.getString(R.string.balance_template,
                currentCurrency.currencySymbol, rate)
        holder.tokenRateDiff.text = 0.toString()
        holder.tokenBalance.text = balance.toString()
        holder.tokenFiatBalance.text = context.getString(R.string.balance_template,
                currentCurrency.currencySymbol, Utils.scaleFloat(balance * rate))

        holder.view.setOnClickListener {
            onItemClickListener.onTokenListItemClick(tokenType)
        }
    }

    override fun unbindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int) {

    }

    interface OnItemClickListener {
        fun onTokenListItemClick(tokenType: TokenType)
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        val tokenImage: CircleImageView? = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenRate: TextView = view.tokenRate
        val tokenRateDiff: TextView = view.tokenRateDiff
        val tokenBalance: TextView = view.tokenBalance
        val tokenFiatBalance: TextView = view.tokenFiatBalance
    }
}