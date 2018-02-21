package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.WalletFlexibleItem
import kotlinx.android.synthetic.main.fragment_wallet_list.*
import java.io.Serializable
import java.util.*

class WalletListFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_wallet_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {

    }

    companion object {
        private val WALLET_LIST_KEY: String = "walletList"

        fun newInstance(walletList: List<String>): WalletListFragment {
            val fragment = WalletListFragment()
            val args = Bundle()
            args.putSerializable(WALLET_LIST_KEY, walletList as Serializable)
            fragment.arguments = args
            return fragment
        }
    }
}