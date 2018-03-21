package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.SearchView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.AddTokenFlexibleItem
import kotlinx.android.synthetic.main.fragment_add_token.*

class AddTokenFragment : Fragment() {

    private lateinit var listener: AddTokenFragmentListener
    private lateinit var itemListener: AddTokenFlexibleItem.OnItemClickListener
    private lateinit var availableTokens: Array<TokenType>
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
        setupRecyclerView()

        itemListener = activity as AddTokenFlexibleItem.OnItemClickListener
        availableTokens = TokenType.values()

        //todo remove added tokens
        availableTokens.forEach {
            if (it == TokenType.ETH) return@forEach

            tokenListAdapter.addItem(AddTokenFlexibleItem(context!!, it, itemListener))
        }

        //todo debug
        val editText = searchToken.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
        editText.setHintTextColor(resources.getColor(R.color.transp_white))
        editText.setTextColor(resources.getColor(R.color.white))
        editText.setHintTextColor(resources.getColor(R.color.white))

        setupListeners()
    }

    private fun setupRecyclerView() {
        tokens = mutableListOf()
        tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
        addTokenList.layoutManager = LinearLayoutManager(context)
        addTokenList.adapter = tokenListAdapter
    }

    private fun setupSearchViewWidth() {
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
            setupSearchViewWidth()
        }

        searchToken.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                setupRecyclerView()

                //todo remove added tokens
                availableTokens.forEach {
                    if (it == TokenType.ETH) return@forEach

                    if (it.codeName.startsWith(newText, true)) {
                        tokenListAdapter.addItem(AddTokenFlexibleItem(context!!, it, itemListener))
                    }
                }
                return false
            }

        })
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