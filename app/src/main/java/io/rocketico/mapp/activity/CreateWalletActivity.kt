package io.rocketico.mapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.activity_create_wallet.*

class CreateWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        init()

        //TODO for debug
        generateWallet.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }
        createNewWallet.setOnClickListener { startActivity(Intent(this, MainActivity::class.java)) }

    }

    private fun init() {
        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimaryDark))
    }
}