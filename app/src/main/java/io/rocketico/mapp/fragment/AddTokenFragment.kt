package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.AddTokenFlexibleItem
import kotlinx.android.synthetic.main.fragment_add_token.*

class AddTokenFragment : Fragment() {

    private lateinit var fragmentListener: AddTokenFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_token, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentListener = activity as AddTokenFragmentListener

        val tokens = mutableListOf<IFlexible<*>>()
        listAdapter = FlexibleAdapter(tokens)

        addTokenList.layoutManager = LinearLayoutManager(context)
        addTokenList.adapter = listAdapter

        val availableTokens = TokenType.values()

        //todo remove added tokens
        availableTokens.forEach {
            if (it == TokenType.ETH) return@forEach

            listAdapter.addItem(AddTokenFlexibleItem(it))
        }

        setupSearchEditText()
        setupListeners()
    }

    private fun setupSearchEditText() {
        val editText = searchToken.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
        editText.setHintTextColor(resources.getColor(R.color.transp_white))
        editText.setTextColor(resources.getColor(R.color.white))
        editText.setHintTextColor(resources.getColor(R.color.white))
    }

    private fun setupSearchViewWidth() {
        searchToken.maxWidth = topPanel.width - backButton.width - menuImageButton.width
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            fragmentListener.onBackClick()
        }

        menuImageButton.setOnClickListener {
            fragmentListener.onMenuButtonClick()
        }

        searchToken.setOnCloseListener {
            tokenListLabel.visibility = View.VISIBLE
            false
        }

        searchToken.setOnSearchClickListener {
            tokenListLabel.visibility = View.GONE
            setupSearchViewWidth()
        }

        listAdapter.addListener(FlexibleAdapter.OnItemClickListener { _, position ->
            val listItem = listAdapter.getItem(position) as AddTokenFlexibleItem
            fragmentListener.onAddTokenListItemClick(listItem.tokenType)
            true
        })

        searchToken.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                listAdapter.setFilter(newText)
                listAdapter.filterItems()
                return false
            }

        })
    }

    interface AddTokenFragmentListener {
        fun onBackClick()
        fun onAddTokenListItemClick(tokenType: TokenType)
        fun onMenuButtonClick()
    }

    companion object {
        fun newInstance(): AddTokenFragment {
            return AddTokenFragment()
        }
    }
}