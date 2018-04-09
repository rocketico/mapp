package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.fragment.FingerPrintFragment

class FingerPrintActivity: AppCompatActivity(),
        FingerPrintFragment.FingerprintFragmentListener{

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)

        supportFragmentManager.beginTransaction()
                .replace(R.id.fingerprintContainer, FingerPrintFragment.newInstance())
                .commit()
    }

    override fun onFingerprintOK() {
        setResult(Cc.FINGERPRINT_RESULT_OK)
        finish()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, FingerPrintActivity::class.java)
        }
    }
}