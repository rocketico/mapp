package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.HistoryFlexibleItem
import io.rocketico.mapp.adapter.TokenFlexibleItem
import kotlinx.android.synthetic.main.fragment_history.*

class HistoryFragment : Fragment() {

    lateinit var historyListAdapter: FlexibleAdapter<IFlexible<*>>
    lateinit var historyItems: MutableList<HistoryFlexibleItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_history, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpRecyclerView()
    }

    private fun setUpRecyclerView() {
        historyItems = mutableListOf()
        historyListAdapter = FlexibleAdapter(historyItems as List<IFlexible<*>>)
        recyclerViewHistory.layoutManager = LinearLayoutManager(context)
        recyclerViewHistory.adapter = historyListAdapter

        //TODO for debug
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
        historyListAdapter.addItem(HistoryFlexibleItem())
    }
}