package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Token
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.activity_import_wallet.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast
import org.jetbrains.anko.uiThread

class ImportWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Utils.setStatusBarColor(this, resources.getColor(R.color.joinColor))
        setContentView(R.layout.activity_import_wallet)
        backButton.setOnClickListener {
            startActivity(CreateWalletActivity.newIntent(this))
            finish()
        }
        importButton.setOnClickListener {
            if (io.rocketico.core.Utils.isPrivateKeyValid(private_key.text.toString())) {
                importPrivateKey()
            } else {
                toast(getString(R.string.wrong_private_key))
            }
        }
    }

    private fun importPrivateKey() {
        val dialog = MaterialDialog.Builder(this)
                .title(getString(R.string.please_wait))
                .content(getString(R.string.generating_new_wallet))
                .progress(true, 0)
                .cancelable(false)
                .show();

        //Delete all wallets, generate and save new wallet
        val wm = WalletManager(this@ImportWalletActivity)
        doAsync({
            runOnUiThread {
                dialog.dismiss()
                toast(getString(R.string.wallet_generation_error) + ": " + it.message)
                it.printStackTrace()
            }
        }) {
            val keyPair = wm.privateKeyToKeyPair(private_key.text.toString())!!
            val walletAddress = wm.publicKeyToAddress(keyPair.publicKey.toString(16))!!

            val walletName = Cc.WALLET_NAME
            val wallet = Wallet(
                    "0x" + walletAddress,
                    keyPair.publicKey.toString(16),
                    walletName
            )
            wallet.privateKey = keyPair.privateKey.toString(16)
            wallet.tokens = mutableListOf()

            if (wm.existsWallet()) {
                wm.deleteWallet()
            }
            wm.saveWallet(wallet)
            uiThread {
                dialog.dismiss()
                startActivity(Intent(this@ImportWalletActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            val intent = Intent(context, ImportWalletActivity::class.java)
            return intent
        }
    }
}
