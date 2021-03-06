package io.rocketico.mapp.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.*
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.activity.SecurityActivity
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

    var paymentTask = PaymentTask()

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

    override fun onDestroy() {
        super.onDestroy()
        paymentTask.cancel()
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        sendPayment.isEnabled = false
        sendPayment.setBackgroundColor(resources.getColor(R.color.grey))
        sendPayment.text = String.format(getString(R.string.sending_wait), 5)
        paymentTask.start();

        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        val rate = RateHelper.getTokenRate(context!!, tokenType, currentCurrency)?.rate
        val ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate
        val txFee = Utils.txFeeFromGwei(gasPrice, ethRate, tokenType)
        val fiatQuantity = rate?.let { eth * it }
        val total = if (fiatQuantity != null && txFee != null) {
            fiatQuantity + txFee
        } else {
            null
        }

        billName.text = getString(R.string.bill_template, tokenType.codeName)
        billAddress.text = address
        billQuantity.text = getString(R.string.balance_template, tokenType.codeName, Utils.scaleFloat(eth))
        billFiatQuantity.text = context!!.setBalanceWithCurrency(fiatQuantity, 2)
        billTxFeeQuantity.text = context!!.setBalanceWithCurrency(txFee, 2)
        billTotal.text = context!!.setBalanceWithCurrency(total, 2)

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
            val password = WalletsPasswordManager.getWalletPassword(WalletManager(context!!).getWallet()?.uuid!!)
            if (password != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    val fpm = context!!.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                    if (!fpm.isHardwareDetected || !fpm.hasEnrolledFingerprints()) {
                        startActivityForResult(SecurityActivity.newIntent(context!!, SecurityActivity.PASSWORD_CODE), Cc.FINGERPRINT_REQUEST)
                    } else {
                        startActivityForResult(SecurityActivity.newIntent(context!!, SecurityActivity.FINGERPRINT_CODE), Cc.FINGERPRINT_REQUEST)
                    }
                }
            } else {
                sendPayment()
            }
        }
    }

    interface SendBillFragmentListener {
        fun onBackClick()
        fun onCloseClick()
    }

    inner class PaymentTask : CountDownTimer(6 * 1000, 1000) {
        override fun onFinish() {
            sendPayment.text = getString(R.string.send_payment_button_text)
            sendPayment.setBackgroundColor(resources.getColor(R.color.colorAccent))
            sendPayment.isEnabled = true
        }

        override fun onTick(p0: Long) {
            sendPayment.text = String.format(getString(R.string.sending_wait), p0 / 1000)
        }

    }

    private fun sendPayment() {
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
                            BigInteger.valueOf(gasPrice.toLong()).multiply(BigInteger.TEN.pow(9)))!!

                    if (!response.isBlank()) {
                        BalanceHelper.outDateBalance(context!!, TokenType.ETH)
                    }
                } else {
                    response = ethHelper.sendErc20(wallet.privateKey,
                            tokenType.contractAddress,
                            address,
                            ethBigInteger,
                            BigInteger.valueOf(gasPrice.toLong()).multiply(BigInteger.TEN.pow(9)))!!.transactionHash

                    if (!response.isBlank()) {
                        BalanceHelper.outDateBalance(context!!, tokenType)
                    }
                }

                view?.context?.runOnUiThread {
                    dialog.dismiss()
                    listener.onCloseClick()
                    context?.longToast(context?.getString(R.string.success_transaction)!!)
                }
            }
        } else {
            context?.toast(getString(R.string.no_internet_connection))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Cc.FINGERPRINT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    sendPayment()
                }
            }
        }
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"
        private const val WALLET_KEY = "wallet_key"
        private const val ETH = "eth"
        private const val ADDRESS = "address"
        private const val GAS_PRICE = "gas_price"

        fun newInstance(wallet: Wallet, tokenType: TokenType, eth: Float, gasPrice: Int, address: String): SendBillFragment {
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