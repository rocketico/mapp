package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.EthereumHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_send_bill.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.uiThread
import java.math.BigInteger

class SendBillFragment : Fragment() {

    private lateinit var listener: SendBillFragmentListener

    private lateinit var tokenType: TokenType
    private var eth: Float = 0f
    private var gasPrice: Int = 0
    private var address: String = ""
    private lateinit var currentCurrency: Currency

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendBillFragmentListener

        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        eth = arguments?.getFloat(ETH)!!
        gasPrice = arguments?.getInt(GAS_PRICE)!!
        //address = arguments?.getString(ADDRESS)!!
        //todo debug. remove me
        address = "0x67f40a629BFc03d0457248aaee5Af7405ACd97d0"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_bill, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        val rate = RateHelper.getTokenRate(context!!,tokenType, currentCurrency)?.rate!!
        val ethRate = RateHelper.getTokenRate(context!!,TokenType.ETH, currentCurrency)?.rate!!
        val txFee = Utils.txFeeFromGwei(gasPrice, ethRate, tokenType)

        billAddress.text = address
        billQuantity.text = eth.toString()
        billFiatQuantity.text = (eth * rate).toString()
        billTxFeeQuantity.text = txFee.toString()
        billTotal.text = ((eth * rate) + txFee).toString()

        setupListeners()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        closeButton.setOnClickListener {
            listener.onCloseClick()
        }

        sendPayment.setOnClickListener {
            val ethBigInteger = Utils.floatToBigInteger(eth, tokenType.decimals)
            val ethHelper = EthereumHelper(Cc.ETH_NODE)
            val wallet = WalletManager(context!!).getWallet()!!

            val dialog = MaterialDialog.Builder(context!!)
                    .title(getString(R.string.please_wait))
                    .content(getString(R.string.sending))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();


            doAsync({
                context?.runOnUiThread {
                    dialog.dismiss()
                    longToast(it.message!!)
                    it.printStackTrace()
                }
            }) {
                val response: String
                if (tokenType == TokenType.ETH) {
                    response = ethHelper.sendEth(wallet.privateKey,
                            address,
                            ethBigInteger,
                            BigInteger.valueOf(gasPrice.toLong()))!!
                } else {
                    response = ethHelper.sendErc20(wallet.privateKey,
                            tokenType.contractAddress,
                            address,
                            ethBigInteger,
                            BigInteger.valueOf(gasPrice.toLong()))!!.transactionHash
                }
                uiThread {
                    dialog.dismiss()
                    listener.onCloseClick()
                    context?.longToast("Success (TX hash $response)") // todo
                }
            }
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
        private const val GAS_PRICE = "gas_price"

        fun newInstance(tokenType: TokenType, eth: Float, gasPrice: Int, address: String): SendBillFragment{
            val fragment = SendBillFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN_TYPE, tokenType)
            bundle.putFloat(ETH, eth)
            bundle.putString(ADDRESS, address)
            bundle.putInt(GAS_PRICE, gasPrice)
            fragment.arguments = bundle
            return fragment
        }
    }
}