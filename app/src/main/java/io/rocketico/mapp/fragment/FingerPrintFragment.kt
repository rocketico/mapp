package io.rocketico.mapp.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pro100svitlo.fingerprintAuthHelper.FahListener
import com.pro100svitlo.fingerprintAuthHelper.FingerprintAuthHelper
import io.rocketico.mapp.R
import com.pro100svitlo.fingerprintAuthHelper.FahErrorType
import kotlinx.android.synthetic.main.fragment_fingerprint.*


class FingerPrintFragment: Fragment(), FahListener {

    private lateinit var fragmentListener: FingerprintFragmentListener
    private lateinit var mFAH: FingerprintAuthHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fragmentListener = activity as FingerprintFragmentListener
        mFAH = FingerprintAuthHelper.Builder(context!!, this).build()
    }

    override fun onResume() {
        super.onResume()
        mFAH.startListening()
    }

    override fun onStop() {
        super.onStop()
        mFAH.stopListening()
    }

    override fun onDestroy() {
        super.onDestroy()
        mFAH.onDestroy()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_fingerprint, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        buttonBack.setOnClickListener {
            fragmentListener.onBackClick()
        }

        passwordButton.setOnClickListener {
            fragmentListener.onEnterPasswordClick()
        }
    }

    override fun onFingerprintListening(listening: Boolean, milliseconds: Long) {

    }

    override fun onFingerprintStatus(authSuccessful: Boolean, errorType: Int, errorMess: CharSequence?) {
        if (authSuccessful) {
            fragmentListener.isFingerprintOK()
        } else {
            when (errorType) {
                FahErrorType.General.LOCK_SCREEN_DISABLED, FahErrorType.General.NO_FINGERPRINTS -> mFAH.showSecuritySettingsDialog()
                FahErrorType.Auth.AUTH_NOT_RECOGNIZED -> {
                }
                FahErrorType.Auth.AUTH_TOO_MANY_TRIES -> {
                }
            }
        }
    }

    interface FingerprintFragmentListener {
        fun onBackClick()
        fun onEnterPasswordClick()
        fun isFingerprintOK()
    }

    companion object {
        fun newInstance(): FingerPrintFragment {
            return FingerPrintFragment()
        }
    }
}