package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.WalletManager
import io.rocketico.core.WalletsPasswordManager
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.fragment.SettingsFragment
import kotlinx.android.synthetic.main.activity_settings.*
import org.jetbrains.anko.toast

class SettingsActivity : AppCompatActivity(),
        SettingsFragment.OnSettingsItemClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimary))

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SettingsFragment.newInstance())
                .commit()

        buttonBack.setOnClickListener {
            finish()
        }
    }

    override fun onExportClick() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val fpm = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
            if (!fpm.isHardwareDetected || !fpm.hasEnrolledFingerprints()) {
                startActivityForResult(FingerPrintActivity.newIntent(this, FingerPrintActivity.PASSWORD_CODE), Cc.FINGERPRINT_REQUEST)
            } else {
                startActivityForResult(FingerPrintActivity.newIntent(this, FingerPrintActivity.FINGERPRINT_CODE), Cc.FINGERPRINT_REQUEST)
            }
        }
    }

    override fun onLogoutClick() {
        val dialog = MaterialDialog.Builder(this)
                .title(getString(R.string.logout))
                .content(getString(R.string.logout_content))
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .onPositive { _, _ ->
                    RateHelper.deleteAllRates(this)
                    BalanceHelper.deleteAllBalances(this)
                    WalletManager(this).deleteWallet()

                    val intent = MainActivity.newIntent(this)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    finish()
                }
                .show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Cc.FINGERPRINT_REQUEST -> {
                if (resultCode == Cc.FINGERPRINT_RESULT_OK) {
                    val wallet = WalletManager(this).getWallet()!!
                    startActivity(Utils.shareTextIntent(wallet.privateKey))
                } else {
                    toast("Not OK!") //todo debug. remove me
                }
            }
        }
    }

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}
