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
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.SendTokenFlexibleItem
import kotlinx.android.synthetic.main.fragment_send.*

class SendFragment : Fragment() {

    private lateinit var fragmentListener: SendFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    private lateinit var walletManager: WalletManager
    private lateinit var wallet: Wallet

    private var address: String? = null

    override fun onResume() {
        super.onResume()
        setQRHandler()
        qr.startCamera()
    }

    override fun onPause() {
        super.onPause()
        qr.stopCamera()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        fragmentListener = activity as SendFragmentListener
        address = null

        val tokens = mutableListOf<IFlexible<*>>()
        listAdapter = FlexibleAdapter(tokens)

        sendTokenList.layoutManager = LinearLayoutManager(context)
        sendTokenList.adapter = listAdapter

        walletManager = WalletManager(context!!)
        wallet = walletManager.getWallet()!!

        setupTokens()
        setupListeners()
    }

    private fun setupTokens() {
        listAdapter.addItem(SendTokenFlexibleItem(context!!, TokenType.ETH))

        wallet.tokens?.forEach {
            if (it.isEther()) return@forEach

            listAdapter.addItem(SendTokenFlexibleItem(context!!, it))
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            fragmentListener.onBackClick()
        }

        qr_frame.setOnClickListener {
            if (qr.visibility == View.GONE) {
                address = null

                fromLabel.visibility = View.GONE
                addressTextView.visibility = View.GONE
                dividerSend.visibility = View.GONE
                hoverLabel.visibility = View.VISIBLE
                qr.visibility = View.VISIBLE

                setQRHandler()
                qr.startCamera()
            }
        }

        listAdapter.addListener(FlexibleAdapter.OnItemClickListener { _, position ->
            val listItem = listAdapter.getItem(position) as SendTokenFlexibleItem
            fragmentListener.onSendTokenListItemClick(listItem.tokenType, address)
            true
        })
    }

    private fun setQRHandler() {
        qr.setResultHandler {
            address = it.text
            addressTextView.text = address

            fromLabel.visibility = View.VISIBLE
            addressTextView.visibility = View.VISIBLE
            dividerSend.visibility = View.VISIBLE
            hoverLabel.visibility = View.GONE
            qr.visibility = View.GONE
        }
        qr.stopCamera()
    }

    interface SendFragmentListener {
        fun onBackClick()
        fun onSendTokenListItemClick(tokenType: TokenType, address: String?)
    }

    companion object {
        fun newInstance(): SendFragment{
            return SendFragment()
        }
    }
}