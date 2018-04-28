package io.rocketico.mapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
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
        val password = WalletsPasswordManager.getWalletPassword(WalletManager(this).getWallet()?.uuid!!)
        if (password != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fpm = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                if (!fpm.isHardwareDetected || !fpm.hasEnrolledFingerprints()) {
                    startActivityForResult(SecurityActivity.newIntent(this, SecurityActivity.PASSWORD_CODE), Cc.FINGERPRINT_REQUEST)
                } else {
                    startActivityForResult(SecurityActivity.newIntent(this, SecurityActivity.FINGERPRINT_CODE), Cc.FINGERPRINT_REQUEST)
                }
            }
        } else {
            exportPrivateKey()
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

    private fun exportPrivateKey() {
        val wallet = WalletManager(this).getWallet()!!
        startActivity(Utils.shareTextIntent(wallet.privateKey))
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            Cc.FINGERPRINT_REQUEST -> {
                if (resultCode == Activity.RESULT_OK) {
                    exportPrivateKey()
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
