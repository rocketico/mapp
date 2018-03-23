package io.rocketico.mapp.fragment

import android.graphics.Paint
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.fragment_receive.*
import net.glxn.qrgen.android.QRCode
import org.jetbrains.anko.toast


class ReceiveFragment : Fragment() {
    lateinit var wallet: Wallet

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_receive, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        wallet = arguments?.getSerializable(WALLET_KEY) as Wallet

        val qrBitmap = QRCode.from(wallet.address).bitmap()
        qr.setImageBitmap(qrBitmap)

        address.text = wallet.address
        address.paintFlags = address.paintFlags or Paint.UNDERLINE_TEXT_FLAG

        qr.setOnClickListener {
            Utils.copyToClipboard(wallet.address, context!!)
            context!!.toast(getString(R.string.copied))
        }

        address.setOnClickListener {
            Utils.copyToClipboard(wallet.address, context!!)
            context!!.toast(getString(R.string.copied))
        }

        buttonBack.setOnClickListener {
            fragmentManager?.popBackStack()
        }

        buttonShare.setOnClickListener {
            startActivity(Utils.shareTextIntent(wallet.address))
        }
    }


    companion object {
        private const val WALLET_KEY = "wallet_key"

        fun newInstance(wallet: Wallet): ReceiveFragment {
            val fragment = ReceiveFragment()
            val bundle = Bundle()
            bundle.putSerializable(WALLET_KEY, wallet)
            fragment.arguments = bundle
            return fragment
        }
    }
}
