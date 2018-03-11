package io.rocketico.mapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric
import io.rocketico.core.WalletManager
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.adapter.TokenFlexibleItem
import io.rocketico.mapp.fragment.MainFragment
import io.rocketico.mapp.fragment.TokenFragment
import org.jetbrains.anko.toast

class MainActivity : AppCompatActivity(),
        MainFragment.MainFragmentListener,
        TokenFlexibleItem.OnItemClickListener,
        TokenFragment.TokenFragmentListener{

    private lateinit var wm: WalletManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fabric.with(this, Crashlytics())

        wm = WalletManager(this)
        val walletList = wm.getWallet()
        if (walletList == null) {
            startActivity(Intent(this, CreateWalletActivity::class.java))
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
                .replace(R.id.container, MainFragment.newInstance())
                .commit()
    }

    override fun onMenuButtonClick() {
        startActivity(Intent(this, MenuActivity::class.java))
        overridePendingTransition(R.anim.anim_slide_up, R.anim.anim_slide_down)
    }

    override fun onFabClick() {
//        TODO for testing
        toast("FAB clicked")

    }

    override fun onTokenListItemClick(position: Int, tokenType: TokenType) {
                supportFragmentManager.beginTransaction()
                .replace(R.id.container, TokenFragment.newInstance(tokenType))
                .addToBackStack(null)
                .commit()
    }

    override fun onBackClick() {
        onBackPressed()
    }

}
