package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.fragment_webview.view.*

class WebViewFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_webview, container, false)
        val url = arguments?.getString(URL)
        view.webView.loadUrl(url)
        view.webView.settings.javaScriptEnabled = true
        view.webView.webViewClient = WebViewClient()
        return view
    }

    companion object {
        private const val URL = "url"

        fun newInstance(url: String): WebViewFragment {
            val fragment = WebViewFragment()
            val args = Bundle()
            args.putString(URL, url)
            fragment.arguments = args
            return fragment
        }
    }
}