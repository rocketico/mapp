package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_menu.*
import kotlinx.android.synthetic.main.menu_logged_out.*

class MenuFragment : Fragment() {

    private lateinit var onMenuButtonsClickListener: OnMenuButtonsClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        onMenuButtonsClickListener = activity as OnMenuButtonsClickListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
    }

    private fun setupListeners() {
        closeButton.setOnClickListener {
            onMenuButtonsClickListener.onCloseClick()
        }

        sendButton.setOnClickListener {
            onMenuButtonsClickListener.onSendClick()
        }

        receiveButton.setOnClickListener {
            onMenuButtonsClickListener.onReceiveClick()
        }

        logInButton.setOnClickListener {
            onMenuButtonsClickListener.onLogInClick()
        }

        joinButton.setOnClickListener {
            onMenuButtonsClickListener.onJoinClick()
        }

        settingsButton.setOnClickListener {
            onMenuButtonsClickListener.onSettingsClick()
        }

        faqButton.setOnClickListener {
            onMenuButtonsClickListener.onFaqClick()
        }

        onlineSuppButton.setOnClickListener {
            onMenuButtonsClickListener.onSupportClick()
        }
    }

    interface OnMenuButtonsClickListener {
        fun onSendClick()
        fun onReceiveClick()
        fun onCloseClick()
        fun onSettingsClick()
        fun onLogInClick()
        fun onJoinClick()
        fun onSupportClick()
        fun onFaqClick()
    }

    companion object {
        fun newInstance(): MenuFragment {
            return MenuFragment()
        }
    }
}