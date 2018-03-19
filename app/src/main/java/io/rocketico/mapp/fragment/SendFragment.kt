package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.*
import io.rocketico.core.model.Token
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenSendFlexibleItem
import kotlinx.android.synthetic.main.fragment_send.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class SendFragment : Fragment() {

    private lateinit var listener: SendFragmentListener
    private lateinit var tokenListAdapter: FlexibleAdapter<IFlexible<*>>
    private lateinit var tokens: MutableList<TokenSendFlexibleItem>

    private lateinit var walletManager: WalletManager
    private lateinit var wallet: Wallet

    private var address: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        listener = activity as SendFragmentListener
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokens = mutableListOf()
        tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
        sendTokenList.layoutManager = LinearLayoutManager(context)
        sendTokenList.adapter = tokenListAdapter

        walletManager = WalletManager(context!!)
        wallet = walletManager.getWallet()!!

        setupTokens()
        setupListeners()
    }

    private fun setupTokens() {
        val itemListener = activity as TokenSendFlexibleItem.OnItemClickListener

        tokenListAdapter.addItem(TokenSendFlexibleItem(context!!, TokenType.ETH, address, itemListener))

        wallet.tokens?.forEach {
            if (it.isEther) return@forEach // skip ether token
            tokenListAdapter.addItem(TokenSendFlexibleItem(context!!, it.type, address, itemListener))
        }
    }

    override fun onResume() {
        super.onResume()
        qr.setResultHandler {
            address = it.text
            addressTextView.text = address
            fromLabel.visibility = View.VISIBLE
            addressTextView.visibility = View.VISIBLE
            qr.visibility = View.GONE

            tokenListAdapter = FlexibleAdapter(tokens as List<IFlexible<*>>)
            sendTokenList.adapter = tokenListAdapter
            setupTokens()
        }
        qr.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qr.stopCamera()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }
    }

    interface SendFragmentListener {
        fun onBackClick()
    }

    companion object {
        fun newInstance(): SendFragment{
            return SendFragment()
        }
    }
}