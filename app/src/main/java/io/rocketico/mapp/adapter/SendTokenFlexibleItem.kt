package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_send_token.view.*

data class SendTokenFlexibleItem(val tokenType: TokenType,
                                 private val currentCurrency: Currency,
                                 private val tokenBalance: Float,
                                 private val tokenRate: Float) :
        AbstractFlexibleItem<SendTokenFlexibleItem.ViewHolder>() {

    override fun getLayoutRes() = R.layout.item_send_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("StringFormatMatches")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val context = holder.itemView.context

        holder.tokenName.text = tokenType.codeName
        holder.tokenBalance.text = tokenBalance.toString()
        holder.tokenFiatBalance.text = context.getString(R.string.balance_template,
                currentCurrency.currencySymbol, Utils.scaleFloat(tokenBalance * tokenRate))
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val tokenImage: CircleImageView? = view.tokenSendImage
        val tokenName: TextView = view.tokenSendName
        val tokenBalance: TextView = view.tokenSendBalance
        val tokenFiatBalance: TextView = view.tokenSendFiatBalance
    }
}