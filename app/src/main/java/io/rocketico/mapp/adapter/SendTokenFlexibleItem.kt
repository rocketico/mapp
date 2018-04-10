package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.BalanceHelper
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.loadIcon
import io.rocketico.mapp.setBalance
import io.rocketico.mapp.setBalanceWithCurrency
import kotlinx.android.synthetic.main.item_send_token.view.*

data class SendTokenFlexibleItem(val tokenType: TokenType,
                                 val currentCurrency: Currency,
                                 val tokenBalance: Float?,
                                 val tokenRate: Float?) :
        AbstractFlexibleItem<SendTokenFlexibleItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_send_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("StringFormatMatches")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val context = holder.itemView.context

        val icon = context.loadIcon(tokenType.codeName)

        if (icon != null) {
            val draw = Drawable.createFromStream(icon, null)
            holder.tokenImage.setImageDrawable(draw)
        }

        holder.tokenName.text = tokenType.codeName

        onBindBalance(holder)
    }

    private fun onBindBalance(holder: ViewHolder) {
        val context = holder.itemView.context
        val flag = BalanceHelper.getMainCurrency(context)
        val fiatBalance = if (tokenBalance != null && tokenRate != null)
            tokenBalance * tokenRate else null

        if (flag) {
            holder.tokenBalance.text = context.setBalance(tokenBalance)
            holder.tokenFiatBalance.text = context.setBalanceWithCurrency(fiatBalance)
        } else {
            holder.tokenBalance.text = context.setBalanceWithCurrency(fiatBalance)
            holder.tokenFiatBalance.text = context.setBalance(tokenBalance)
        }
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val tokenImage: CircleImageView = view.tokenSendImage
        val tokenName: TextView = view.tokenSendName
        val tokenBalance: TextView = view.tokenSendBalance
        val tokenFiatBalance: TextView = view.tokenSendFiatBalance
    }
}