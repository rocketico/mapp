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
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
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
    private var fiatBalance: Float = 0f

    private var ethQuantity: Float = 0f
    private var fiatQuantity: Float = 0f
    private var txFee: Float = 0.5f

    private lateinit var prefix: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendDetailsFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        address = arguments?.getString(ADDRESS)

        prefix = getString(R.string.prefix_template, tokenType.codeName)

        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        rate = RateHelper.getTokenRate(context!!,tokenType, currentCurrency)?.rate!!
        balance = Utils.bigIntegerToFloat(BalanceHelper.loadTokenBalance(context!!, tokenType)!!)
        fiatBalance = balance * rate

        tokenName.text = tokenType.codeName
        tokenBalance.text = balance.toString()
        tokenFiatBalance.text = fiatBalance.toString()

        if (address != null) {
            addressEditText.setText(address)
        }

        quantityEditText.setText(prefix)

        quantityFiatTextView.text = fiatQuantity.toString()
        txFeeTextView.text = txFee.toString()
        totalTextView.text = txFee.toString()

        setupListeners()
        setupSeekBar()
    }

    @SuppressLint("StringFormatMatches")
    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        createButton.setOnClickListener {
            if (checkForNulls()) {
                listener.onCreateClick(tokenType, ethQuantity, addressEditText.text.toString())
            }
        }

        changeButton.setOnClickListener {
            if (prefix == getString(R.string.prefix_template, tokenType.codeName)) {
                prefix = getString(R.string.prefix_template, currentCurrency.currencySymbol)
                quantityEditText.setText(getString(R.string.quantity_template, prefix, fiatQuantity))
            } else {
                prefix = getString(R.string.prefix_template, tokenType.codeName)
                quantityEditText.setText(getString(R.string.quantity_template, prefix, ethQuantity))
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
                    quantityFiatTextView.text = fiatQuantity.toString()
                } else {
                    quantityFiatTextView.text = ethQuantity.toString()
                }
                totalTextView.text = (txFee + fiatQuantity).toString()
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

        })
    }

    private fun setupSeekBar() {
        seekBar.max = balance.toInt()
    }

    //todo debug
    private fun checkForNulls(): Boolean {
        if (addressEditText.text.isBlank()) {
            context?.toast("Address is empty")
            return false
        }
        return true
    }

    interface SendDetailsFragmentListener {
        fun onBackClick()
        fun onCreateClick(tokenType: TokenType, eth: Float, address: String)
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