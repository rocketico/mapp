package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.core.WalletManager
import io.rocketico.core.WalletsPasswordManager
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_password.*

class PasswordFragment: Fragment() {

    private lateinit var fragmentListener: PasswordFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as PasswordFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                val password = WalletsPasswordManager.getWalletPassword(WalletManager(context!!).getWallet()?.uuid!!)
                if (s.toString() == password) {
                    fragmentListener.isFingerprintOK()
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        buttonBack.setOnClickListener {
            fragmentListener.onBackClick()
        }
    }

    interface PasswordFragmentListener {
        fun onBackClick()
        fun isFingerprintOK()
    }

    companion object {
        fun newInstance(): PasswordFragment {
            return PasswordFragment()
        }
    }
}