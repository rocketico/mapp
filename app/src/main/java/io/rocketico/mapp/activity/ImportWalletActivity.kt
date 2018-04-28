package io.rocketico.mapp.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.WalletManager
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.loadData
import kotlinx.android.synthetic.main.activity_import_wallet.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class ImportWalletActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_import_wallet)
        backButton.setOnClickListener {
            startActivity(CreateWalletActivity.newIntent(this))
            finish()
        }
        importButton.setOnClickListener {
            if (WalletManager.isPrivateKeyValid(private_key.text.toString())) {
                importPrivateKey()
            } else {
                toast(getString(R.string.wrong_private_key))
            }
        }
    }

    private fun importPrivateKey() {
        val dialog = MaterialDialog.Builder(this)
                .title(getString(R.string.please_wait))
                .content(getString(R.string.importing_wallet))
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

            val walletTokens = loadData { WalletManager.getWalletTokens(wallet.address) }
            if (walletTokens != null) {
                val tokenTypes = TokenType.values()
                val tokens = mutableSetOf<TokenType>()
                walletTokens.forEach { responseToken ->
                    tokenTypes.forEach {
                        if (it.codeName.toLowerCase() == responseToken.symbol.toLowerCase()) {
                            tokens.add(it)
                        }
                    }
                }

                wallet.tokens?.addAll(tokens)
            }

            if (wm.existsWallet()) {
                wm.deleteWallet()
            }
            wm.saveWallet(wallet)
            runOnUiThread {
                dialog.dismiss()
                startActivity(Intent(this@ImportWalletActivity, MainActivity::class.java))
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setupQR()
    }

    private fun setupQR() {
        qr.setResultHandler {
            if (WalletManager.isPrivateKeyValid(it.text)) {
                private_key.setText(it.text)
                qr.visibility = View.GONE
            } else {
                toast(getString(R.string.invalid_address))
                setupQR()
            }
        }
        qr.startCamera()
    }

    override fun onPause() {
        super.onPause()

        qr.stopCamera()
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, ImportWalletActivity::class.java)
        }
    }
}
