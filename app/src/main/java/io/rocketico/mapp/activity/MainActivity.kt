package io.rocketico.mapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import com.crashlytics.android.Crashlytics
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.fabric.sdk.android.Fabric
import io.rocketico.core.RateHelper
import io.rocketico.core.WalletManager
import io.rocketico.core.WalletsPasswordManager
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.fragment.AddItemFragment
import io.rocketico.mapp.fragment.AddTokenFragment
import io.rocketico.mapp.fragment.MainFragment
import io.rocketico.mapp.fragment.TokenFragment
import org.jetbrains.anko.toast


class MainActivity : BaseSecureActivity(),
        MainFragment.MainFragmentListener,
        TokenFragment.TokenFragmentListener,
        AddItemFragment.AddItemFragmentListener {

    private lateinit var wm: WalletManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())
        Logger.addLogAdapter(AndroidLogAdapter())

        wm = WalletManager(this)
        val walletList = wm.getWallet()
        if (walletList == null) {
            startActivity(CreateWalletActivity.newIntent(this))
            finish()
            return
        }

        val password = WalletsPasswordManager.getWalletPassword(walletList.uuid)
        if (password != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fpm = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                if (!fpm.isHardwareDetected || !fpm.hasEnrolledFingerprints()) {
                    startActivityForResult(SecurityActivity.newIntent(this, SecurityActivity.PASSWORD_CODE), Cc.FINGERPRINT_REQUEST)
                } else {
                    startActivityForResult(SecurityActivity.newIntent(this, SecurityActivity.FINGERPRINT_CODE), Cc.FINGERPRINT_REQUEST)
                }
            }
        }

        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimary))

        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit()
    }

    override fun onMenuButtonClick() {
        startActivity(MenuActivity.newIntent(this))
        overridePendingTransition(R.anim.anim_slide_up, R.anim.anim_slide_down)
    }

    override fun onFabClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, AddItemFragment.newInstance(wm.getWallet()!!))
                .addToBackStack(null)
                .commit()
    }

    override fun onTokenListItemClick(tokenType: TokenType) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, TokenFragment.newInstance(wm.getWallet()!!, tokenType))
                .addToBackStack(null)
                .commit()
    }

    override fun onTokenListItemSwipe(tokenType: TokenType) {
        startActivity(MenuActivity.newIntent(this, MenuActivity.ACTION_SENT, tokenType))
    }

    override fun onAddTokenListItemClick(tokenType: TokenType) {
        val wallet = wm.getWallet()!!
        wallet.tokens?.add(tokenType)
        wm.saveWallet(wallet)
        toast(getString(R.string.added_string))
        RateHelper.deleteAllRates(this)
        onBackPressed()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Cc.FINGERPRINT_REQUEST -> {
                if (resultCode != Activity.RESULT_OK) {
                    finish()
                }
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
