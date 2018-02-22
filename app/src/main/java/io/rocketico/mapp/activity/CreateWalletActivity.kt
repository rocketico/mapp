package io.rocketico.mapp.activity

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.activity_create_wallet.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread

class CreateWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        init()
    }

    private fun init() {
        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimaryDark))


        createNewWallet.setOnClickListener {
            val dialog = MaterialDialog.Builder(this)
                    .title("Please wait")
                    .content("Generating new wallet...")
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            //Delete all wallets, generate and save new wallet
            val wm = WalletManager(this@CreateWalletActivity)
            doAsync {
                val keyPair = wm.generatePrivateKey()!!
                val walletAddress = wm.publicKeyToAddress(keyPair.publicKey.toString(16))!!

                val walletName = Cc.WALLET_NAME
                val wallet = Wallet(
                        "0x" + walletAddress,
                        keyPair.publicKey.toString(16),
                        walletName
                )
                wallet.privateKey = keyPair.privateKey.toString(16)
                wm.deleteAllWallets()
                wm.saveWallet(wallet)
                uiThread {
                    dialog.dismiss()
                    startActivity(Intent(this@CreateWalletActivity, MainActivity::class.java))
                    finish()
                }
            }

        }
    }
}
