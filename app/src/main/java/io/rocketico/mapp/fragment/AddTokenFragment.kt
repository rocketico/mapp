package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.AddTokenFlexibleItem
import kotlinx.android.synthetic.main.fragment_add_token.*

class AddTokenFragment : Fragment() {

    private lateinit var listener: AddTokenFragmentListener
    private lateinit var tokenListAdapter: FlexibleAdapter<IFlexible<*>>
    private lateinit var tokens: MutableList<AddTokenFlexibleItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as AddTokenFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_token, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokens = mutableListOf()
        tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
        addTokenList.layoutManager = LinearLayoutManager(context)
        addTokenList.adapter = tokenListAdapter

        val itemListener = activity as AddTokenFlexibleItem.OnItemClickListener

        val availableTokens = TokenType.values()

        availableTokens.forEach {
            if (it == TokenType.ETH) return@forEach

            tokenListAdapter.addItem(AddTokenFlexibleItem(context!!, it, itemListener))
        }

        setupListeners()
    }

    private fun setupSearchView() {
        searchToken.maxWidth = topPanel.width - backButton.width - menuImageButton.width
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        menuImageButton.setOnClickListener {
            listener.onMenuButtonClick()
        }

        searchToken.setOnCloseListener {
            tokenListLabel.visibility = View.VISIBLE
            false
        }

        searchToken.setOnSearchClickListener {
            tokenListLabel.visibility = View.GONE
            setupSearchView()
        }


    }

    interface AddTokenFragmentListener {
        fun onBackClick()
        fun onMenuButtonClick()
    }

    companion object {
        fun newInstance(): AddTokenFragment {
            return AddTokenFragment()
        }
    }
}