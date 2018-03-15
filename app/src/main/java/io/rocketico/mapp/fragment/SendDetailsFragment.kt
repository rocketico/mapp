package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.Utils
import io.rocketico.core.model.Currency
import io.rocketico.core.model.Token
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_send_details.*
//todo debug
class SendDetailsFragment : Fragment() {

    private lateinit var listener: SendDetailsFragmentListener
    private lateinit var tokenType: TokenType
    private lateinit var currentCurrency: Currency
    private var rate: Float? = 0f
    private var balance: Float? = 0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendDetailsFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenType = arguments?.getSerializable(TOKEN_TYPE) as TokenType
        currentCurrency = RateHelper.getCurrentCurrency(context!!)
        rate = RateHelper.getTokenRate(context!!,tokenType, currentCurrency)?.rate
        balance = Utils.bigIntegerToFloat(BalanceHelper.loadTokenBalance(context!!, tokenType)!!)

        tokenName.text = tokenType.codeName
        tokenBalance.text = balance.toString()
        fiatBalance.text = (balance!! * rate!!).toString()
        //todo debug
        quantityFiat.text = "0.0"
        txFeeQuantity.text = "0.5"
        totalCount.text = "0.5"

        setupListeners()
        setupSeekBar()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        createButton.setOnClickListener {
            val f = quantity.text.toString()
            listener.onCreateClick(tokenType, f.toFloat(), address.text.toString())
        }

        quantity.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrBlank()){
                    //todo debug
                    quantityFiat.text = "0.0"
                    totalCount.text = "0.5"
                } else {
                    val value = s.toString()
                    val q = value.toFloat() * rate!!
                    quantityFiat.text = q.toString()
                    totalCount.text = (q + 0.5f).toString()
                }
            }

        })
    }

    private fun setupSeekBar() {
        seekBar.max = balance?.toInt()!!
    }

    interface SendDetailsFragmentListener {
        fun onBackClick()
        fun onCreateClick(tokenType: TokenType, eth: Float, address: String)
    }

    companion object {
        private const val TOKEN_TYPE = "token_type"

        fun newInstance(tokenType: TokenType): SendDetailsFragment{
            val fragment = SendDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN_TYPE, tokenType)
            fragment.arguments = bundle
            return fragment
        }
    }
}