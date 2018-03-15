package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.core.RateHelper
import io.rocketico.core.model.Token
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_send_bill.*

class SendBillFragment : Fragment() {

    private lateinit var listener: SendBillFragmentListener

    private lateinit var tokenType: TokenType
    private var eth: Float = 0f
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendBillFragmentListener

        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        eth = arguments?.getFloat(ETH)!!
        address = arguments?.getString(ADDRESS)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val rate = RateHelper.getTokenRate(context!!,tokenType, RateHelper.getCurrentCurrency(context!!))?.rate

        billAddress.text = address
        billQuantity.text = eth.toString()
        billFiatQuantity.text = (eth * rate!!).toString()
        billTxFeeQuantity.text = "0.5"
        billTotal.text = ((eth * rate) + 0.5f).toString()

        setupListeners()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        closeButton.setOnClickListener {
            listener.onCloseClick()
        }
    }

    interface SendBillFragmentListener {
        fun onBackClick()
        fun onCloseClick()
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"
        private const val ETH = "eth"
        private const val ADDRESS = "address"

        fun newInstance(tokenType: TokenType, eth: Float, address: String): SendBillFragment{
            val fragment = SendBillFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN_TYPE, tokenType)
            bundle.putFloat(ETH, eth)
            bundle.putString(ADDRESS, address)
            fragment.arguments = bundle
            return fragment
        }
    }
}