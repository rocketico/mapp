package io.rocketico.mapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.*
import io.rocketico.mapp.event.MainCurrencyEvent
import io.rocketico.mapp.event.UpdateEvent
import kotlinx.android.synthetic.main.item_token.view.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

data class TokenFlexibleItem(private val context: Context,
                             val tokenType: TokenType) :
        AbstractFlexibleItem<TokenFlexibleItem.ViewHolder>() {

    var tokenBalance: Float? = null
    var tokenRate: Float? = null
    var tokenRateDiff: Float? = null

    private lateinit var mHolder: ViewHolder

    init {
        EventBus.getDefault().register(this)
    }

    override fun onViewDetached(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder?, position: Int) {
        EventBus.getDefault().unregister(this)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onEvent(event: MainCurrencyEvent) {
        onBindBalance(mHolder)
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onUpdateEvent(event: UpdateEvent) {
        val balance = BalanceHelper.loadTokenBalance(context, tokenType)
        val rate = RateHelper.getTokenRate(context, tokenType, RateHelper.getCurrentCurrency(context))?.rate
        val yesterdayRate = RateHelper.getYesterdayTokenRate(context, tokenType, RateHelper.getCurrentCurrency(context))?.rate
        tokenBalance = balance?.let { Utils.bigIntegerToFloat(it, tokenType.decimals) }
        tokenRate = rate
        tokenRateDiff = io.rocketico.mapp.Utils.calculateDifference(rate, yesterdayRate)
    }

    override fun isSwipeable(): Boolean = true

    override fun getLayoutRes() = R.layout.item_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<*>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        mHolder = holder

        val icon = context.loadIcon(tokenType.codeName)

        if (icon != null) {
            val draw = Drawable.createFromStream(icon, null)
            holder.tokenImage.setImageDrawable(draw)
        } else {
            holder.tokenImage.setImageDrawable(context.resources.getDrawable(R.mipmap.ic_launcher_round))
        }

        holder.tokenName.text = tokenType.toString()
        holder.tokenRate.text = context.setBalanceWithCurrency(tokenRate, 2)
        holder.tokenRateDiff.text = context.setRateDifference(tokenRateDiff)

        if (tokenRateDiff != null) {
            holder.rateDirection.visibility = View.VISIBLE

            when {
                tokenRateDiff!! < 0f -> {
                    holder.tokenRateDiff.setTextColor(context.resources
                            .getColor(R.color.colorAccent))
                    holder.rateDirection.setImageDrawable(context.resources
                            .getDrawable(R.drawable.ic_direction_down))
                    holder.rateDirection.setColorFilter(context.resources
                            .getColor(R.color.colorAccent))
                }
                tokenRateDiff!! >= 0.01f -> {
                    holder.tokenRateDiff.setTextColor(context.resources
                            .getColor(R.color.colorReceive))
                    holder.rateDirection.setImageDrawable(context.resources
                            .getDrawable(R.drawable.ic_direction_up))
                    holder.rateDirection.setColorFilter(context.resources
                            .getColor(R.color.colorReceive))
                }
                else -> {
                    holder.rateDirection.visibility = View.INVISIBLE
                    holder.tokenRateDiff.setTextColor(context.resources
                            .getColor(android.R.color.darker_gray))
                }
            }
        } else {
            holder.rateDirection.visibility = View.INVISIBLE
            holder.tokenRateDiff.setTextColor(context.resources
                    .getColor(android.R.color.darker_gray))
        }

        onBindBalance(holder)
    }

    private fun onBindBalance(holder: ViewHolder) {
        val flag = BalanceHelper.getMainCurrency(context)
        val fiatBalance = if (tokenBalance != null && tokenRate != null)
            tokenBalance!! * tokenRate!! else null

        if (flag) {
            holder.tokenBalance.text = context.setBalance(tokenBalance)
            holder.tokenFiatBalance.text = context.setBalanceWithCurrency(fiatBalance,2)
        } else {
            holder.tokenBalance.text = context.setBalanceWithCurrency(fiatBalance, 2)
            holder.tokenFiatBalance.text = context.setBalance(tokenBalance)
        }
    }

    class ViewHolder(val view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {
        val tokenImage: CircleImageView = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenRate: TextView = view.tokenRate
        val rateDirection: ImageView = view.direction
        val tokenRateDiff: TextView = view.tokenRateDiff
        val tokenBalance: TextView = view.tokenBalance
        val tokenFiatBalance: TextView = view.tokenFiatBalance

        override fun getFrontView(): View {
            return view.findViewById(R.id.frontView)
        }

        override fun getRearLeftView(): View {
            return view.findViewById(R.id.rearSentLiftView)
        }

        override fun getRearRightView(): View {
            return view.findViewById(R.id.rearReceiveRightView)
        }
    }
}