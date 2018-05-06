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
import io.rocketico.core.RateHelper
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
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
        fragmentListener = parentFragment as AddTokenFragmentListener

        val tokens = mutableListOf<IFlexible<*>>()
        listAdapter = FlexibleAdapter(tokens)

        addTokenList.layoutManager = LinearLayoutManager(context)
        addTokenList.adapter = listAdapter

        val availableTokens = TokenType.values()
        val wallet = arguments?.getSerializable(WALLET_KEY) as Wallet
        val currentCurrency = RateHelper.getCurrentCurrency(context!!)

        availableTokens.forEach {
            if (it == TokenType.ETH) return@forEach
            if (wallet.tokens?.contains(it)!!) return@forEach

            listAdapter.addItem(AddTokenFlexibleItem(it, currentCurrency))
        }

        fragmentListener.setupTokenListAdapter(listAdapter)

        setupListeners()
    }

    private fun setupListeners() {
        listAdapter.addListener(FlexibleAdapter.OnItemClickListener { _, position ->
            val listItem = listAdapter.getItem(position) as AddTokenFlexibleItem
            fragmentListener.onAddTokenListItemClick(listItem.tokenType)
            true
        })
    }

    interface AddTokenFragmentListener {
        fun onAddTokenListItemClick(tokenType: TokenType)
        fun setupTokenListAdapter(adapter: FlexibleAdapter<IFlexible<*>>)
    }

    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet): AddTokenFragment {
            val fragment = AddTokenFragment()
            val args = Bundle()

            args.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = args

            return fragment
        }
    }
}