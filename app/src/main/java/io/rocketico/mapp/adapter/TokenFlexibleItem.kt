package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.BalanceHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.test.MainCurrencyEvent
import kotlinx.android.synthetic.main.item_token.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

data class TokenFlexibleItem(val tokenType: TokenType,
                             var currentCurrency: Currency,
                             var tokenBalance: Float,
                             var tokenRate: Float) :
        AbstractFlexibleItem<TokenFlexibleItem.ViewHolder>() {

    private lateinit var mHolder: ViewHolder

    override fun onViewAttached(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder?, position: Int) {
        EventBus.getDefault().register(this)
    }

    override fun onViewDetached(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder?, position: Int) {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MainCurrencyEvent) {
        onBindBalance(mHolder)
    }

    override fun getLayoutRes() = R.layout.item_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    @SuppressLint("StringFormatMatches")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        val context = holder.itemView.context
        mHolder = holder
        
        holder.tokenName.text = tokenType.toString()
        holder.tokenRate.text = context.getString(R.string.balance_template,
                currentCurrency.currencySymbol, tokenRate)
        holder.tokenRateDiff.text = "0" //todo add rates difference
        onBindBalance(holder)
    }

    private fun onBindBalance(holder: ViewHolder) {
        val context = holder.itemView.context
        val flag = BalanceHelper.getMainCurrency(context)

        if (flag) {
            holder.tokenBalance.text = tokenBalance.toString()
            holder.tokenFiatBalance.text = context.getString(R.string.balance_template,
                    currentCurrency.currencySymbol, Utils.scaleFloat(tokenBalance * tokenRate))
        } else {
            holder.tokenBalance.text = context.getString(R.string.balance_template,
                    currentCurrency.currencySymbol, Utils.scaleFloat(tokenBalance * tokenRate))
            holder.tokenFiatBalance.text = tokenBalance.toString()
        }
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