package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_add_password.*

class AddPasswordFragment : Fragment() {
    private lateinit var fragmentListener: AddPasswordFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as AddPasswordFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
       buttonBack.setOnClickListener {
           fragmentListener.onBackClick()
       }

       createPasswordButton.setOnClickListener {
           if (passwordEditText.text.toString() == confirmPasswordEditText.text.toString()){
               fragmentListener.onCreateClick(passwordEditText.text.toString())
           }
       }
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