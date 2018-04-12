package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.helper.ItemTouchHelper
import com.crashlytics.android.Crashlytics
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.fabric.sdk.android.Fabric
import io.rocketico.core.WalletManager
import io.rocketico.core.WalletsPasswordManager
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.fragment.AddTokenFragment
import io.rocketico.mapp.fragment.MainFragment
import io.rocketico.mapp.fragment.TokenFragment
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(),
        MainFragment.MainFragmentListener,
        TokenFragment.TokenFragmentListener,
        AddTokenFragment.AddTokenFragmentListener {

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

        if (WalletsPasswordManager.getWalletPassword(walletList.uuid) == null) {
            startActivity(FingerPrintActivity.newIntent(this, FingerPrintActivity.ADD_PASSWORD_CODE))
            finish()
            return
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                val fpm = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                if (!fpm.isHardwareDetected || !fpm.hasEnrolledFingerprints()) {
                    startActivityForResult(FingerPrintActivity.newIntent(this, FingerPrintActivity.PASSWORD_CODE), Cc.FINGERPRINT_REQUEST)
                } else {
                    startActivityForResult(FingerPrintActivity.newIntent(this, FingerPrintActivity.FINGERPRINT_CODE), Cc.FINGERPRINT_REQUEST)
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
                .replace(R.id.container, AddTokenFragment.newInstance(wm.getWallet()!!))
                .addToBackStack(null)
                .commit()
    }

    override fun onTokenListItemClick(tokenType: TokenType) {
                supportFragmentManager.beginTransaction()
                .replace(R.id.container, TokenFragment.newInstance(wm.getWallet()!!, tokenType))
                .addToBackStack(null)
                .commit()
    }

    override fun onTokenListItemSwipe(tokenType: TokenType, position: Int, direction: Int) {
        when(direction) {
            ItemTouchHelper.RIGHT -> {
                startActivity(MenuActivity.newIntent(this, MenuActivity.ACTION_SENT, tokenType))
            }
            ItemTouchHelper.LEFT -> {
                startActivity(MenuActivity.newIntent(this, MenuActivity.ACTION_RECEIVE))
            }
        }
    }

    override fun onAddTokenListItemClick(tokenType: TokenType) {
        val wallet = wm.getWallet()!!
        wallet.tokens?.add(tokenType)
        wm.saveWallet(wallet)
        toast(getString(R.string.added_string))
        onBackPressed()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when(requestCode) {
            Cc.FINGERPRINT_REQUEST -> {
                if (resultCode != Cc.FINGERPRINT_RESULT_OK) {
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
