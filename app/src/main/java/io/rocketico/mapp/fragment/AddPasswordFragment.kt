package io.rocketico.mapp.fragment

import android.content.Context
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_add_password.*
import org.jetbrains.anko.toast

class AddPasswordFragment : Fragment() {

    private lateinit var fragmentListener: AddPasswordFragmentListener
    private var password: String = ""
    private var confirmPassword: String = ""

    private var isPasswordEntered: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as AddPasswordFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        focusPassword()

        buttonBack.setOnClickListener {
            fragmentListener.onBackClick()
        }

        buttonCheck.setOnClickListener {
            if (!isPasswordEntered) {
                password = passwordEditText.text.toString()
                isPasswordEntered = true

                enterPasswordTextView.text = getString(R.string.confirm_password_label)
                passwordEditText.text.clear()

                focusPassword()
            } else {
                confirmPassword = passwordEditText.text.toString()
                if (password == confirmPassword) {
                    fragmentListener.onCreateClick(password)
                } else {
                    context!!.toast(getString(R.string.no_match))
                }
            }
        }
    }

    private fun focusPassword() {
        passwordEditText.postDelayed({
            passwordEditText.requestFocus()
            val imm = context!!.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(passwordEditText, InputMethodManager.SHOW_IMPLICIT)
        }, 50)
    }

    interface AddPasswordFragmentListener {
        fun onBackClick()
        fun onCreateClick(password: String)
    }

    companion object {
        fun newInstance(): AddPasswordFragment {
            return AddPasswordFragment()
        }
    }
}