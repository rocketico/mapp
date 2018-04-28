package io.rocketico.mapp.adapter

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.loadIcon
import kotlinx.android.synthetic.main.item_add_token.view.*

data class AddTokenFlexibleItem(val tokenType: TokenType,
                                private val currentCurrency: Currency,
                                private val tokenRate: Float = 0.02f,
                                private val tokenMarket: String = "Bitrix") :
        AbstractFlexibleItem<AddTokenFlexibleItem.AddTokenViewHolder>(),
        IFilterable<String> {

    override fun filter(constraint: String): Boolean {
        if (tokenType.codeName.startsWith(constraint, true)) return true
        return false
    }

    override fun getLayoutRes() = R.layout.item_add_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): AddTokenViewHolder {
        return AddTokenViewHolder(view, adapter)
    }

    @SuppressLint("StringFormatMatches")
    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: AddTokenViewHolder, position: Int, payloads: MutableList<Any>) {
        val context = holder.itemView.context

        val icon = context.loadIcon(tokenType.codeName)

        if (icon != null) {
            val draw = Drawable.createFromStream(icon, null)
            holder.tokenImage.setImageDrawable(draw)
        } else {
            holder.tokenImage.setImageDrawable(context.resources.getDrawable(R.mipmap.ic_launcher_round))
        }

        holder.tokenName.text = tokenType.codeName
        holder.tokenCoast.text = context.getString(R.string.balance_template, currentCurrency, tokenRate)
        holder.tokenMarket.text = tokenMarket
    }

    class AddTokenViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val tokenImage: CircleImageView = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenCoast: TextView = view.tokenCoast
        val tokenMarket: TextView = view.tokenMarket
    }
}