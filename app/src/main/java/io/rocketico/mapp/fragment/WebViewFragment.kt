package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.fragment_webview.*

class WebViewFragment : Fragment() {

    private lateinit var fragmentListener: WebViewFragmentListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentListener = activity as WebViewFragmentListener
        Utils.setStatusBarColor(activity!!, resources.getColor(R.color.colorPrimary))
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_webview, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val code = arguments?.getInt(CODE)
        val url: String = Cc.ROCKETICO_URL + when (code) {
            FAQ_CODE -> FAQ_URL
            SUPPORT_CODE -> SUPPORT_URL
            else -> throw IllegalArgumentException()
        }

        settingsLabel.text = when (code) {
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

        private const val FAQ_URL = "faq"
        private const val SUPPORT_URL = "support"

        fun newInstance(code: Int): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putInt(CODE, code)
            fragment.arguments = args
            return fragment
        }
    }
}