package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_token.view.*

data class TokenFlexibleItem(val tokenType: TokenType,
                             var currentCurrency: Currency,
                             var tokenBalance: Float,
                             var tokenRate: Float) :
        AbstractFlexibleItem<TokenFlexibleItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("StringFormatMatches")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val context = holder.itemView.context
        
        holder.tokenName.text = tokenType.toString()
        holder.tokenRate.text = context.getString(R.string.balance_template,
                currentCurrency.currencySymbol, tokenRate)
        holder.tokenRateDiff.text = "0" //todo add rates difference
        holder.tokenBalance.text = tokenBalance.toString()
        holder.tokenFiatBalance.text = context.getString(R.string.balance_template,
                currentCurrency.currencySymbol, Utils.scaleFloat(tokenBalance * tokenRate))
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val tokenImage: CircleImageView? = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenRate: TextView = view.tokenRate
        val tokenRateDiff: TextView = view.tokenRateDiff
        val tokenBalance: TextView = view.tokenBalance
        val tokenFiatBalance: TextView = view.tokenFiatBalance
    }
}