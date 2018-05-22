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
import io.rocketico.mapp.adapter.FundFlexibleItem
import kotlinx.android.synthetic.main.fragment_add_fund.*

class AddFundFragment : Fragment() {

    private lateinit var fragmentListener: AddFundFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_fund, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentListener = parentFragment as AddFundFragmentListener

        val tokens = mutableListOf<IFlexible<*>>()
        listAdapter = FlexibleAdapter(tokens)

        fundList.layoutManager = LinearLayoutManager(context)
        fundList.adapter = listAdapter

        //add founds here

        fragmentListener.setupFundListAdapter(listAdapter)
    }

    interface AddFundFragmentListener {
        fun setupFundListAdapter(adapter: FlexibleAdapter<IFlexible<*>>)
    }

    companion object {
        fun newInstance(): AddFundFragment {
            return AddFundFragment()
        }
    }
}