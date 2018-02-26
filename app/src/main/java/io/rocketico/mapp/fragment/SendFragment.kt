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
import io.rocketico.mapp.adapter.TokenSendFlexibleItem
import kotlinx.android.synthetic.main.fragment_send.*

class SendFragment : Fragment() {

    lateinit var tokenListAdapter: FlexibleAdapter<IFlexible<*>>
    lateinit var tokens: MutableList<TokenSendFlexibleItem>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokens = mutableListOf()
        tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
        sendTokenList.layoutManager = LinearLayoutManager(context)
        sendTokenList.adapter = tokenListAdapter

        val listener = activity as TokenSendFlexibleItem.OnItemClickListener

        //TODO for testing
        tokenListAdapter.addItem(TokenSendFlexibleItem(listener))
        tokenListAdapter.addItem(TokenSendFlexibleItem(listener))
        tokenListAdapter.addItem(TokenSendFlexibleItem(listener))
        tokenListAdapter.addItem(TokenSendFlexibleItem(listener))
    }
}