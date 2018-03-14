package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_send_details.*

class SendDetailsFragment : Fragment() {

    private lateinit var listener: SendDetailsFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        listener = activity as SendDetailsFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_send_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        setupListeners()
    }

    private fun setupListeners() {
        backButton.setOnClickListener {
            listener.onBackClick()
        }

        createButton.setOnClickListener {
            listener.onCreateClick()
        }
    }

    interface SendDetailsFragmentListener {
        fun onBackClick()
        fun onCreateClick()
    }

    companion object {
        fun newInstance(): SendDetailsFragment{
            return SendDetailsFragment()
        }
    }
}