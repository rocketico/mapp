package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.design.widget.TabLayout
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentStatePagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_add_item.*

class AddItemFragment : Fragment(),
        AddTokenFragment.AddTokenFragmentListener,
        AddFundFragment.AddFundFragmentListener {

    private lateinit var wallet: Wallet
    private lateinit var fragmentListener: AddItemFragmentListener

    private lateinit var tokenListAdapter: FlexibleAdapter<IFlexible<*>>
//    private lateinit var fundListAdapter: FlexibleAdapter<IFlexible<*>>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_item, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        arguments?.let { wallet = it.getSerializable(WALLET_KEY) as Wallet }
        fragmentListener = activity as AddItemFragmentListener

        viewPagerAdd.adapter = object : FragmentStatePagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment = when (position) {
                0 -> AddTokenFragment.newInstance(wallet)
//                1 -> AddFundFragment.newInstance()
                else -> throw IllegalArgumentException()
            }

            override fun getCount(): Int = 1

//            override fun getCount(): Int = 2
        }

        setupSearchEditText()
        setupListeners()
    }

    private fun setupSearchEditText() {
        val editText = searchItem.findViewById<EditText>(android.support.v7.appcompat.R.id.search_src_text)
        editText.setHintTextColor(resources.getColor(R.color.transp_white))
        editText.setTextColor(resources.getColor(R.color.white))
        editText.setHintTextColor(resources.getColor(R.color.white))
    }

    private fun setupSearchViewWidth() {
        searchItem.maxWidth = topPanel.width - backButton.width - menuImageButton.width
    }

    private fun setupListeners() {
//        viewPagerAdd.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabsAdd))
//        tabsAdd.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(viewPagerAdd))

        backButton.setOnClickListener {
            fragmentListener.onBackClick()
        }

        menuImageButton.setOnClickListener {
            fragmentListener.onMenuButtonClick()
        }

        searchItem.setOnCloseListener {
            tokenListLabel.visibility = View.VISIBLE
            false
        }

        searchItem.setOnSearchClickListener {
            tokenListLabel.visibility = View.GONE
            setupSearchViewWidth()
        }

        searchItem.setOnQueryTextListener(object : android.support.v7.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                tokenListAdapter.setFilter(newText)
                tokenListAdapter.filterItems()

//                fundListAdapter.setFilter(newText)
//                fundListAdapter.filterItems()

                return false
            }

        })
    }

    override fun onAddTokenListItemClick(tokenType: TokenType) {
        fragmentListener.onAddTokenListItemClick(tokenType)
    }

    override fun setupTokenListAdapter(adapter: FlexibleAdapter<IFlexible<*>>) {
        tokenListAdapter = adapter
    }

    override fun setupFundListAdapter(adapter: FlexibleAdapter<IFlexible<*>>) {
//        fundListAdapter = adapter
    }

    interface AddItemFragmentListener {
        fun onBackClick()
        fun onAddTokenListItemClick(tokenType: TokenType)
        fun onMenuButtonClick()
    }

    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet): AddItemFragment {
            val fragment = AddItemFragment()
            val args = Bundle()

            args.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = args

            return fragment
        }
    }
}