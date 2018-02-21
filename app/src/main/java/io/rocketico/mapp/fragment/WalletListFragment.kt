package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.uxname.uxwalletcore.model.Wallet
import eu.davidea.flexibleadapter.FlexibleAdapter
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.WalletFlexibleItem
import kotlinx.android.synthetic.main.fragment_wallet_list.*
import java.io.Serializable
import java.util.*

class WalletListFragment : Fragment() {

    private lateinit var walletList: List<Wallet>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        walletList = listOf(Wallet("address1", "public_key1", "name1"),
                Wallet("address2", "public_key2", "name2"))
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_wallet_list, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        val dataList : MutableList<WalletFlexibleItem> = mutableListOf(WalletFlexibleItem(walletList[0]),
                WalletFlexibleItem(walletList[1]))
        val adapter = FlexibleAdapter(dataList)

        walletListView.layoutManager = LinearLayoutManager(context)
        walletListView.adapter = adapter
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