package io.rocketico.mapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import io.rocketico.mapp.R

data class FundFlexibleItem(val boolean: Boolean) : AbstractFlexibleItem<FundFlexibleItem.FundViewHolder>() {

    override fun getLayoutRes(): Int = R.layout.item_fund

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>): FundViewHolder {
        return FundViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: FundViewHolder, position: Int, payloads: MutableList<Any>) {

    }

    class FundViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<*>>) : FlexibleViewHolder(view, adapter) {

    }
}