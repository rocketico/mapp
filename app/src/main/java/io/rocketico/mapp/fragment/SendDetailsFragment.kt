package io.rocketico.mapp.fragment

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.Selection
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.SeekBar
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.*
import io.rocketico.mapp.activity.QrScanActivity
import kotlinx.android.synthetic.main.fragment_send_details.*
import org.jetbrains.anko.toast
import java.math.BigInteger

class SendDetailsFragment : Fragment() {

    private lateinit var fragmentListener: SendDetailsFragmentListener
    private lateinit var tokenType: TokenType
    private lateinit var currentCurrency: Currency

    private lateinit var ethBalance: BigInteger
    private var ethRate: Float? = 0f

    private var balance: Float = 0f
    private var fiatBalance: Float? = 0f
    private var rate: Float? = 0f

    private lateinit var address: String
    private var quantity: Float = 0f
    private var gasPriceGwei: Int = 0

    private lateinit var tokenPrefix: String
    private lateinit var fiatPrefix: String

    private var isTokenMain = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as SendDetailsFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        address = arguments?.getString(ADDRESS) ?: ""

        currentCurrency = RateHelper.getCurrentCurrency(context!!)

        ethBalance = BalanceHelper.loadTokenBalance(context!!, TokenType.ETH)!!
        ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate

        balance = Utils.bigIntegerToFloat(BalanceHelper.loadTokenBalance(context!!, tokenType)!!)
        rate = RateHelper.getTokenRate(context!!, tokenType, currentCurrency)?.rate
        fiatBalance = rate?.let { balance * it }

        tokenPrefix = getString(R.string.prefix_template, tokenType.codeName)
        fiatPrefix = getString(R.string.prefix_template, currentCurrency.currencySymbol)

        addressEditText.setText(address)

        tokenName.text = tokenType.codeName
        tokenBalance.text = context!!.setBalance(balance)
        tokenFiatBalance.text = context!!.setBalanceWithCurrency(fiatBalance, 2)

        if (rate == null) {
            changeButton.isEnabled = false
        }

        setupListeners()
        setupSeekBar()

        quantityEditText.setText(context!!.setQuantity(tokenPrefix, 0f))
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            fragmentListener.onBackClick()
        }

        createButton.setOnClickListener {
            if (checkForErrors()) {
                fragmentListener.onCreateClick(tokenType, quantity, gasPriceGwei, address)
            }
        }

        changeButton.setOnClickListener {
            if (isTokenMain) {
                quantityEditText.removeTextChangedListener(tokenCurrencyListener)
                quantityEditText.addTextChangedListener(fiatCurrencyListener)

                changeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_right))
            } else {
                quantityEditText.removeTextChangedListener(fiatCurrencyListener)
                quantityEditText.addTextChangedListener(tokenCurrencyListener)

                changeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_left))
            }
            isTokenMain = !isTokenMain

            seekBar.progress = seekBar.progress

            quantityEditText.setText(quantityFiatTextView.text)
            Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
        }

        quantityEditText.addTextChangedListener(tokenCurrencyListener)

        quantityEditText.setOnTouchListener { _, event ->
            quantityEditText.onTouchEvent(event)
            quantityEditText.setSelection(quantityEditText.text.length)
            true
        }

        addressEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                address = s.toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        qrScan.setOnClickListener {
            if (ContextCompat.checkSelfPermission(context!!,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST)

            } else {
                startActivityForResult(QrScanActivity.newIntent(context!!), ADDRESS_REQUEST)
            }
        }
    }

    private fun setupSeekBar() {
        seekBar.max = GAS_PRICE_MAX

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                gasPriceGwei = progress + 1
                val txFee: Float?
                val totalQuantity: Float?
                val fiatQuantity = rate?.let { quantity * it }

                if (isTokenMain) {
                    txFee = Utils.ethFromGwei(gasPriceGwei)
                    val tmpQuantity = if (tokenType == TokenType.ETH) quantity else RateHelper.convertCurrency(rate, ethRate, quantity)
                    totalQuantity = tmpQuantity?.let { it + txFee }

                    txFeeTextView.text = context!!.setTokenBalance(TokenType.ETH.codeName, txFee, 9)
                    totalTextView.text = context!!.setTokenBalance(TokenType.ETH.codeName, totalQuantity, 9)
                } else {
                    txFee = Utils.txFeeFromGwei(gasPriceGwei, ethRate, tokenType)
                    totalQuantity = if (txFee != null && fiatQuantity != null)
                        fiatQuantity + txFee else null

                    txFeeTextView.text = context!!.setBalanceWithCurrency(txFee, 2)
                    totalTextView.text = context!!.setBalanceWithCurrency(totalQuantity, 2)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        seekBar.progress = if (tokenType == TokenType.ETH)
            DEFAULT_ETHER_TX_FEE else DEFAULT_TOKEN_TX_FEE
    }

    private fun checkForErrors(): Boolean {
        if (address.isBlank()) {
            context!!.toast(getString(R.string.address_error))
            return false
        }
        if (!WalletManager.isValidAddress(address)) {
            context!!.toast(getString(R.string.invalid_address))
            return false
        }
        if (quantity == 0f) {
            context!!.toast(getString(R.string.payment_error))
            return false
        }
        if (checkBalance()) {
            context?.toast(context?.getString(R.string.balance_error)!!)
            return false
        }

        return true
    }

    private fun checkBalance(): Boolean {
        var paymentCoast = BigInteger.valueOf(gasPriceGwei.toLong()).multiply(BigInteger.TEN.pow(9))
        if (tokenType == TokenType.ETH) paymentCoast += Utils.floatToBigInteger(quantity)

        return ethBalance - paymentCoast <= BigInteger.ZERO
    }

    private val tokenCurrencyListener = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            if (!s.toString().startsWith(tokenPrefix)) {
                quantityEditText.setText(tokenPrefix)
                Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                return
            }

            val value = s?.toString()!!.replace(tokenPrefix, "")

            quantity = if (!value.isBlank()) {
                if (value == ".") {
                    quantityEditText.setText(tokenPrefix)
                    Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                    return
                }

                val result = value.toFloat()

                if (result > balance) {
                    quantityEditText.setText(context!!.setQuantity(tokenPrefix, balance))
                    Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                    return
                }
                result
            } else {
                0f
            }

            val fiatQuantity = rate?.let { quantity * it }
            val txFee = Utils.ethFromGwei(gasPriceGwei)
            val tmpQuantity = if (tokenType == TokenType.ETH) quantity else RateHelper.convertCurrency(rate, ethRate, quantity)
            val totalQuantity = tmpQuantity?.let { it + txFee }

            quantityFiatTextView.text = context!!.setQuantity(fiatPrefix, fiatQuantity, 2)
            txFeeTextView.text = context!!.setTokenBalance(TokenType.ETH.codeName, txFee, 9)
            totalTextView.text = context!!.setTokenBalance(TokenType.ETH.codeName, totalQuantity, 9)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    private val fiatCurrencyListener = object : TextWatcher {

        override fun afterTextChanged(s: Editable?) {
            if (!s.toString().startsWith(fiatPrefix)) {
                quantityEditText.setText(fiatPrefix)
                Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                return
            }

            val value = s?.toString()!!.replace(fiatPrefix, "")

            quantity = if (!value.isBlank()) {
                if (value == ".") {
                    quantityEditText.setText(fiatPrefix)
                    Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                    return
                }

                val result = value.toFloat()

                if (result > fiatBalance!!) {
                    quantityEditText.setText(context!!.setQuantity(fiatPrefix, fiatBalance!!))
                    Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                    return
                }
                result / rate!!
            } else {
                0f
            }

            val fiatQuantity = quantity * rate!!
            val txFee = Utils.txFeeFromGwei(gasPriceGwei, ethRate, tokenType)
            val totalQuantity = if (txFee != null)
                fiatQuantity + txFee else null

            quantityFiatTextView.text = context!!.setQuantity(tokenPrefix, quantity, 6)
            txFeeTextView.text = context!!.setBalanceWithCurrency(txFee, 2)
            totalTextView.text = context!!.setBalanceWithCurrency(totalQuantity, 2)
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            ADDRESS_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    address = data!!.getStringExtra(Intent.EXTRA_RETURN_RESULT)
                    addressEditText.setText(address)
                }
            }
            else -> {
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    startActivityForResult(QrScanActivity.newIntent(context!!), ADDRESS_REQUEST)
                } else {

                    context!!.toast(getString(R.string.permission_denied))

                }
                return
            }
            else -> {
            }
        }
    }

    interface SendDetailsFragmentListener {
        fun onBackClick()
        fun onCreateClick(tokenType: TokenType, eth: Float, gasPrice: Int, address: String)
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"
        private const val ADDRESS = "address"

        private const val GAS_PRICE_MAX = 98
        private const val DEFAULT_ETHER_TX_FEE = 40
        private const val DEFAULT_TOKEN_TX_FEE = 21

        private const val ADDRESS_REQUEST = 1
        private const val CAMERA_PERMISSION_REQUEST = 0

        fun newInstance(tokenType: TokenType, address: String?): SendDetailsFragment {
            val fragment = SendDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN_TYPE, tokenType)
            bundle.putString(ADDRESS, address)
            fragment.arguments = bundle
            return fragment
        }
    }
}