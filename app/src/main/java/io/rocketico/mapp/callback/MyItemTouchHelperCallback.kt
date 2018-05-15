package io.rocketico.mapp.callback

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import eu.davidea.flexibleadapter.helpers.ItemTouchHelperCallback
import io.rocketico.mapp.adapter.TokenFlexibleItem

class MyItemTouchHelperCallback(adapter: AdapterCallback) : ItemTouchHelperCallback(adapter) {
    override fun getMovementFlags(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?): Int {
        return if (viewHolder is TokenFlexibleItem.ViewHolder && viewHolder.isEther) {
            makeMovementFlags(0, ItemTouchHelper.RIGHT)
        } else {
            super.getMovementFlags(recyclerView, viewHolder)
        }
    }
}