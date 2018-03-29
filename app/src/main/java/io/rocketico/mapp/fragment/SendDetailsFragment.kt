package io.rocketico.mapp.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.support.v4.app.Fragment
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
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_send_details.*
import org.jetbrains.anko.toast

class SendDetailsFragment : Fragment() {

    private lateinit var listener: SendDetailsFragmentListener
    private lateinit var tokenType: TokenType
    private lateinit var currentCurrency: Currency
    private var address: String? = null

    private var balance: Float = 0f
    private var rate: Float = 0f
    private var ethRate: Float = 0f
    private var fiatBalance: Float = 0f

    private var ethQuantity: Float = 0f
    private var fiatQuantity: Float = 0f
    private var txFee: Float = 0.5f

    private lateinit var prefix: String

    private val gasPriceMax = 98

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendDetailsFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_details, container, false)
    }

    @SuppressLint("StringFormatMatches")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        address = arguments?.getString(ADDRESS)

        prefix = getString(R.string.prefix_template, tokenType.codeName)

        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        rate = RateHelper.getTokenRate(context!!,tokenType, currentCurrency)?.rate!!
        ethRate = RateHelper.getTokenRate(context!!, TokenType.ETH, currentCurrency)?.rate!!
        balance = Utils.bigIntegerToFloat(BalanceHelper.loadTokenBalance(context!!, tokenType)!!)
        fiatBalance = balance * rate

        tokenName.text = tokenType.codeName
        tokenBalance.text = balance.toString()
        tokenFiatBalance.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, Utils.scaleFloat(fiatBalance))

        if (address != null) {
            addressEditText.setText(address)
        }

        quantityEditText.setText(prefix)

        quantityFiatTextView.text = getString(R.string.balance_template,
                currentCurrency.currencySymbol, Utils.scaleFloat(fiatQuantity))
        txFeeTextView.text = getString(R.string.balance_template, currentCurrency.currencySymbol,
                Utils.scaleFloat(txFee))

        setupListeners()
        setupSeekBar()
    }

    @SuppressLint("StringFormatMatches")
    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        createButton.setOnClickListener {
            if (checkForErrors()) {
                listener.onCreateClick(tokenType, ethQuantity, seekBar.progress + 1, addressEditText.text.toString())
            }
        }

        changeButton.setOnClickListener {
            if (prefix == getString(R.string.prefix_template, tokenType.codeName)) {
                prefix = getString(R.string.prefix_template, currentCurrency.currencySymbol)
                quantityEditText.setText(getString(R.string.quantity_template, prefix, fiatQuantity))
                changeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_right))
            } else {
                prefix = getString(R.string.prefix_template, tokenType.codeName)
                quantityEditText.setText(getString(R.string.quantity_template, prefix, ethQuantity))
                changeButton.startAnimation(AnimationUtils.loadAnimation(context, R.anim.rotate_left))
            }
            Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
        }

        quantityEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if(!s.toString().startsWith(prefix)){
                    quantityEditText.setText(prefix)
                    Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                    return
                }

                val value = s?.toString()!!.replace(prefix, "")

                if (!value.isBlank()) {
                    if (prefix == getString(R.string.prefix_template, tokenType.codeName)) {
                        if (value.toFloat() > balance) {
                            quantityEditText.setText(getString(R.string.quantity_template, prefix, balance))
                            Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                            return
                        }
                        ethQuantity = value.toFloat()
                        fiatQuantity = ethQuantity * rate
                    } else {
                        if (value.toFloat() > fiatBalance) {
                            quantityEditText.setText(getString(R.string.quantity_template, prefix, fiatBalance))
                            Selection.setSelection(quantityEditText.text, quantityEditText.text.length)
                            return
                        }
                        fiatQuantity = value.toFloat()
                        ethQuantity = fiatQuantity / rate
                    }
                } else {
                    ethQuantity = 0f
                    fiatQuantity = 0f
                }

                if (prefix == tokenType.codeName + " ") {
                    quantityFiatTextView.text = getString(R.string.balance_template,
                            currentCurrency.currencySymbol, Utils.scaleFloat(fiatQuantity))
                } else {
                    quantityFiatTextView.text = getString(R.string.balance_template,
                            tokenType.codeName, Utils.scaleFloat(ethQuantity))
                }
                totalTextView.text = getString(R.string.balance_template,
                        currentCurrency.currencySymbol, Utils.scaleFloat(txFee + fiatQuantity))
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }
    @SuppressLint("StringFormatMatches")
    private fun setupSeekBar() {
        seekBar.max = gasPriceMax

        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                txFee = Utils.txFeeFromGwei(progress + 1, ethRate, tokenType)
                txFeeTextView.text = getString(R.string.balance_template,
                        currentCurrency.currencySymbol, Utils.scaleFloat(txFee))
                totalTextView.text = getString(R.string.balance_template,
                        currentCurrency.currencySymbol, Utils.scaleFloat(txFee + fiatQuantity))
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {

            }

        })

        seekBar.progress = if (tokenType == TokenType.ETH) 40 else 21
    }

    private fun checkForErrors(): Boolean {
        if (addressEditText.text.isBlank()) {
            context?.toast(context?.getString(R.string.address_error)!!)
            return false
        }
        if (!WalletManager.isValidAddress(addressEditText.text.toString())){
            context?.toast(getString(R.string.invalid_address))
            return false
        }
        if (ethQuantity == 0f) {
            context?.toast(context?.getString(R.string.payment_error)!!)
            return false
        }
        if (fiatQuantity + txFee > fiatBalance) {
            context?.toast(context?.getString(R.string.balance_error)!!)
            return false
        }

        return true
    }

    interface SendDetailsFragmentListener {
        fun onBackClick()
        fun onCreateClick(tokenType: TokenType, eth: Float, gasPrice: Int, address: String)
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"
        private const val ADDRESS = "address"

        fun newInstance(tokenType: TokenType, address: String?): SendDetailsFragment{
            val fragment = SendDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN_TYPE, tokenType)
            bundle.putString(ADDRESS, address)
            fragment.arguments = bundle
            return fragment
        }
    }
}