package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import io.rocketico.core.*
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.SendTokenFlexibleItem
import kotlinx.android.synthetic.main.fragment_send.*

class SendFragment : Fragment() {

    private lateinit var fragmentListener: SendFragmentListener
    private lateinit var listAdapter: FlexibleAdapter<IFlexible<*>>

    private lateinit var wallet: Wallet
    private lateinit var currentCurrency: Currency

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

        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet

        setupTokens()
        setupListeners()
    }

    private fun setupTokens() {
        currentCurrency = RateHelper.getCurrentCurrency(context!!)

        val ethBalance = BalanceHelper.loadTokenBalance(context!!, TokenType.ETH)!!
        val ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate!!
        listAdapter.addItem(SendTokenFlexibleItem(TokenType.ETH, currentCurrency,
                Utils.bigIntegerToFloat(ethBalance), ethRate))

        wallet.tokens?.forEach {
            if (it.isEther()) return@forEach

            val tokenBalance = BalanceHelper.loadTokenBalance(context!!, it)!!
            val tokenRate = RateHelper.getTokenRate(context!!, it, currentCurrency)?.rate!!
            listAdapter.addItem(SendTokenFlexibleItem(it, currentCurrency,
                    Utils.bigIntegerToFloat(tokenBalance), tokenRate))
        }
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            fragmentListener.onBackClick()
        }

        qr_frame.setOnClickListener {
            if (qrContent.visibility == View.GONE) {
                address = null

                addressContent.visibility = View.GONE
                qrContent.visibility = View.VISIBLE

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

            addressContent.visibility = View.VISIBLE
            qrContent.visibility = View.GONE
        }
        qr.stopCamera()
    }

    interface SendFragmentListener {
        fun onBackClick()
        fun onSendTokenListItemClick(tokenType: TokenType, address: String?)
    }

    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet): SendFragment{
            val fragment = SendFragment()
            val args = Bundle()

            args.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = args

            return fragment
        }
    }
}