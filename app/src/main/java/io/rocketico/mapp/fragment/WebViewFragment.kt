package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_webview.*

class WebViewFragment : Fragment() {

    private lateinit var fragmentListener: WebViewFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentListener = activity as WebViewFragmentListener
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val code = arguments?.getInt(CODE)
        val url: String = when(code) {
            FAQ_CODE -> Cc.FAQ_URL
            SUPPORT_CODE -> Cc.SUPPORT_URL
            else -> throw IllegalArgumentException()
        }

        settingsLabel.text = when(code) {
            FAQ_CODE -> getString(R.string.faq_button)
            SUPPORT_CODE -> getString(R.string.online_support_button)
            else -> throw IllegalArgumentException()
        }

        webView.loadUrl(url)
        webView.settings.javaScriptEnabled = true
        webView.webViewClient = WebViewClient()

        buttonBack.setOnClickListener {
            fragmentListener.onBackClick()
        }
    }

    interface WebViewFragmentListener {
        fun onBackClick()
    }

    companion object {
        private const val CODE = "code"
        const val FAQ_CODE = 0
        const val SUPPORT_CODE = 1

        fun newInstance(code: Int): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putInt(CODE, code)
            fragment.arguments = args
            return fragment
        }
    }
}