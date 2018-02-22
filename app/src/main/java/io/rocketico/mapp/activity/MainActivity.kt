package io.rocketico.mapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.core.WalletManager
import io.rocketico.mapp.R
import io.rocketico.mapp.fragment.MainFragment

class MainActivity : AppCompatActivity(),
        MainFragment.MainFragmentListener {
    private lateinit var wm: WalletManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        wm = WalletManager(this)
        val walletList = wm.getWalletIdList()
        if (walletList == null || walletList.isEmpty()) {
            startActivity(Intent(this, CreateWalletActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        init()
    }

    override fun onMenuButtonClick() {
        startActivity(Intent(this, SlidingUpMenuActivity::class.java))
        overridePendingTransition(R.anim.anim_slide_up, R.anim.anim_slide_down)
    }

    override fun onFabClick() {
        //transaction to AddNewTokenFragment
    }

    private fun init() {
        supportActionBar?.setDisplayShowTitleEnabled(false)

        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MainFragment.newInstance())
                .commit()
    }
}
