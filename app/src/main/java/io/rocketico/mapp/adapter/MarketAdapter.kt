package io.rocketico.mapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_spinner.view.*

class MarketAdapter(context: Context, val list: List<String>) : BaseAdapter() {

    private val inflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = inflater.inflate(R.layout.item_spinner, null)
        view.marketName.text = list[position]
        return view
    }

    override fun getItem(position: Int): Any {
        return list[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return list.size
    }

}
