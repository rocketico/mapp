package io.rocketico.mapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import io.rocketico.core.model.Currency
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.item_spinner.view.*
import kotlinx.android.synthetic.main.item_spinner_dropdown.view.*

class FiatCurrencySpinnerAdapter(private val context: Context, private val list: Array<Currency>)
    : BaseAdapter() {

    private var mainPosition: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        mainPosition = position

        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_spinner, parent, false)
        view.symbol.text = list[position].currencySymbol
        view.codeName.text = list[position].codeName
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val view = inflater.inflate(R.layout.item_spinner_dropdown, parent, false)
        if (position == mainPosition) {
            view.setBackgroundColor(context.resources.getColor(R.color.colorPrimaryDark))

            view.symbolDD.setTextColor(context.resources.getColor(R.color.white))
            view.lineDD.setTextColor(context.resources.getColor(R.color.white))
            view.codeNameDD.setTextColor(context.resources.getColor(R.color.white))
        }
        view.symbolDD.text = list[position].currencySymbol
        view.codeNameDD.text = list[position].codeName
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
