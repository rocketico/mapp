package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_menu.*

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
            onMenuButtonsClickListener.onBackClick()
        }

        sendButton.setOnClickListener {
            onMenuButtonsClickListener.onSendClick()
        }
    }

    interface OnMenuButtonsClickListener {
        fun onSendClick()
        fun onBackClick()
    }
}