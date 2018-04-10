package io.rocketico.mapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.core.WalletManager
import io.rocketico.mapp.R
import kotlinx.android.synthetic.main.activity_qr_scan.*
import org.jetbrains.anko.toast

class QrScanActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(android.R.style.Theme_Light_NoTitleBar_Fullscreen)
        setContentView(R.layout.activity_qr_scan)
    }

    override fun onResume() {
        super.onResume()
        startCamera()
    }

    override fun onPause() {
        super.onPause()
        qrScan.stopCamera()
    }

    private fun startCamera() {
        qrScan.startCamera()
        qrScan.setResultHandler {
            val address = it.text
            if (WalletManager.isValidAddress(address)) {
                val resultIntent = Intent()
                resultIntent.putExtra(Intent.EXTRA_RETURN_RESULT, address)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            } else {
                toast(getString(R.string.invalid_address))
                startCamera()
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, QrScanActivity::class.java)
            return intent
        }
    }
}