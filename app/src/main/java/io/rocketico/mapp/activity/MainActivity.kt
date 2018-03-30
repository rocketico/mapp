package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import com.orhanobut.logger.AndroidLogAdapter
import com.orhanobut.logger.Logger
import io.fabric.sdk.android.Fabric
import io.rocketico.core.WalletManager
import io.rocketico.core.model.TokenType
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

        setContentView(R.layout.activity_main)
        init()
    }

    private fun init() {
        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimary))

        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance(wm.getWallet()!!))
                .commit()
    }

    override fun onMenuButtonClick() {
        startActivity(Intent(this, MenuActivity::class.java))
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

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }
}
