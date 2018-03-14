package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.core.model.Token
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_send_details.*

class SendDetailsFragment : Fragment() {

    private lateinit var listener: SendDetailsFragmentListener
    private lateinit var token: Token

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendDetailsFragmentListener
        token = arguments?.getSerializable(TOKEN) as Token
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tokenName.text = token.type.codeName
        tokenBalance.text = token.balance!!.toString()
        fiatBalance.text = (token.balance!! * token.rate!!).toString()
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
            listener.onCreateClick(token, f.toFloat(), address.text.toString())
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
                    val q = value.toFloat() * token.rate!!
                    quantityFiat.text = q.toString()
                    totalCount.text = (q + 0.5f).toString()
                }
            }

        })
    }

    private fun setupSeekBar() {
        seekBar.max = token.balance?.toInt()!!
    }

    interface SendDetailsFragmentListener {
        fun onBackClick()
        fun onCreateClick(token: Token, eth: Float, address: String)
    }

    companion object {
        private const val TOKEN = "Token"

        fun newInstance(token: Token): SendDetailsFragment{
            val fragment = SendDetailsFragment()
            val bundle = Bundle()
            bundle.putSerializable(TOKEN, token)
            fragment.arguments = bundle
            return fragment
        }
    }
}