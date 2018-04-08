package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.BalanceHelper
import io.rocketico.core.RateHelper
import io.rocketico.core.WalletManager
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
        val wallet = WalletManager(this).getWallet()!!
        startActivity(Utils.shareTextIntent(wallet.privateKey))
    }

    override fun onLogoutClick() {
        val dialog = MaterialDialog.Builder(this)
                .title(getString(R.string.logout))
                .content(getString(R.string.logout_content))
                .positiveText(getString(R.string.yes))
                .negativeText(getString(R.string.no))
                .onPositive { dialog, which ->
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

    companion object {
        fun getIntent(context: Context): Intent {
            return Intent(context, SettingsActivity::class.java)
        }
    }
}
