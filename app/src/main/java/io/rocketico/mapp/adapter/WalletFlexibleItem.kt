package io.rocketico.mapp.adapter

import android.support.v7.widget.RecyclerView
import android.view.View
import com.uxname.uxwalletcore.model.Wallet
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.mapp.R

class WalletFlexibleItem(private val wallet: Wallet) : IFlexible<WalletFlexibleItem.ViewHolder> {

    override fun isSelectable(): Boolean = false

    override fun bindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int, payloads: MutableList<Any>?) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onViewDetached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun shouldNotifyChange(newItem: IFlexible<*>?): Boolean = false

    override fun setSwipeable(swipeable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getItemViewType(): Int = 0

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<out IFlexible<*>>?): ViewHolder {
        return ViewHolder(view)
    }

    override fun onViewAttached(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isDraggable(): Boolean = false

    override fun setSelectable(selectable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isEnabled(): Boolean = true

    override fun getBubbleText(position: Int): String = wallet.name[0].toString()

    override fun setEnabled(enabled: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun setHidden(hidden: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun isHidden(): Boolean = false

    override fun isSwipeable(): Boolean = false

    override fun unbindViewHolder(adapter: FlexibleAdapter<out IFlexible<*>>?, holder: ViewHolder?, position: Int) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getSpanSize(spanCount: Int, position: Int): Int = 1

    override fun getLayoutRes(): Int = R.layout.item_wallet

    override fun setDraggable(draggable: Boolean) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    class ViewHolder(var view: View) : RecyclerView.ViewHolder(view) {
        //val balanceTextView: TextView = view.subtitle
        //val walletName: TextView = view.walletName
        //val walletId: TextView = view.walletId
    }
}