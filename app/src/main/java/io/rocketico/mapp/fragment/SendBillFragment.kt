package io.rocketico.mapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.BalanceHelper
import io.rocketico.core.EthereumHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.setBalanceWithCurrency
import kotlinx.android.synthetic.main.fragment_send_bill.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.longToast
import org.jetbrains.anko.runOnUiThread
import org.jetbrains.anko.toast
import java.math.BigInteger

class SendBillFragment : Fragment() {

    private lateinit var listener: SendBillFragmentListener

    private lateinit var tokenType: TokenType
    private lateinit var currentCurrency: Currency
    private lateinit var wallet: Wallet

    private var eth: Float = 0f
    private var gasPrice: Int = 0
    private var address: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendBillFragmentListener

        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet

        eth = arguments?.getFloat(ETH)!!
        gasPrice = arguments?.getInt(GAS_PRICE)!!
        address = arguments?.getString(ADDRESS)!!
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_bill, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        val rate = RateHelper.getTokenRate(context!!,tokenType, currentCurrency)?.rate
        val ethRate = RateHelper.getTokenRate(context!!,TokenType.ETH, currentCurrency)?.rate
        val txFee = ethRate?.let { Utils.txFeeFromGwei(gasPrice, it, tokenType) }
        val fiatQuantity = rate?.let { eth * it }
        val total = if (fiatQuantity != null && txFee != null) {
            fiatQuantity + txFee
        } else {
            null
        }

        billName.text = getString(R.string.bill_template, tokenType.codeName)
        billAddress.text = address
        billQuantity.text = getString(R.string.balance_template, tokenType.codeName, Utils.scaleFloat(eth))
        billFiatQuantity.text = context!!.setBalanceWithCurrency(fiatQuantity)
        billTxFeeQuantity.text = context!!.setBalanceWithCurrency(txFee)
        billTotal.text = context!!.setBalanceWithCurrency(total)

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

            if (io.rocketico.mapp.Utils.isOnline(context!!)) {
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

                        if (!response.isBlank()) {
                            BalanceHelper.outDateBalance(context!!, TokenType.ETH)
                        }
                    } else {
                        response = ethHelper.sendErc20(wallet.privateKey,
                                tokenType.contractAddress,
                                address,
                                ethBigInteger,
                                BigInteger.valueOf(gasPrice.toLong()))!!.transactionHash

                        if (!response.isBlank()) {
                            BalanceHelper.outDateBalance(context!!, tokenType)
                        }
                    }

                    view?.context?.runOnUiThread {
                        dialog.dismiss()
                        listener.onCloseClick()
                        context?.longToast(context?.getString(R.string.success_transaction, response)!!)
                    }
                }
            } else {
                context?.toast(getString(R.string.no_internet_connection))
            }
        }
    }

    interface SendBillFragmentListener {
        fun onBackClick()
        fun onCloseClick()
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"
        private const val WALLET_KEY = "wallet_key"
        private const val ETH = "eth"
        private const val ADDRESS = "address"
        private const val GAS_PRICE = "gas_price"

        fun newInstance(wallet: Wallet, tokenType: TokenType, eth: Float, gasPrice: Int, address: String): SendBillFragment{
            val fragment = SendBillFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN_TYPE, tokenType)
            bundle.putSerializable(WALLET_KEY, wallet)
            bundle.putFloat(ETH, eth)
            bundle.putString(ADDRESS, address)
            bundle.putInt(GAS_PRICE, gasPrice)
            fragment.arguments = bundle
            return fragment
        }
    }
}