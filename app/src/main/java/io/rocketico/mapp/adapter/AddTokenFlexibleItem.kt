package io.rocketico.mapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import de.hdodenhof.circleimageview.CircleImageView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFilterable
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_add_token.view.*

data class AddTokenFlexibleItem(val tokenType: TokenType) :
        AbstractFlexibleItem<AddTokenFlexibleItem.AddTokenViewHolder>(),
        IFilterable<String>{

    override fun filter(constraint: String): Boolean {
        if (tokenType.codeName.startsWith(constraint, true)) return true
        return false
    }

    override fun getLayoutRes() = R.layout.item_add_token

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): AddTokenViewHolder {
        return AddTokenViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: AddTokenViewHolder, position: Int, payloads: MutableList<Any>) {
        //todo debug
        holder.tokenName.text = tokenType.codeName
        holder.tokenCoast.text = "0.01"
        holder.tokenMarket.text = "Bitrix"
    }

    class AddTokenViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {
        val tokenImage: CircleImageView? = view.tokenImage
        val tokenName: TextView = view.tokenName
        val tokenCoast: TextView = view.tokenCoast
        val tokenMarket: TextView = view.tokenMarket
    }
}