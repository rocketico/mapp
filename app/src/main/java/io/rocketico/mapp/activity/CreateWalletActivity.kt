package io.rocketico.mapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.activity_create_wallet.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class CreateWalletActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        init()
    }

    private fun init() {
        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimaryDark))

        importWallet.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                        CAMERA_PERMISSION_REQUEST)

            } else {
                startImportWalletActivity()
            }
        }
        createNewWallet.setOnClickListener {
            val dialog = MaterialDialog.Builder(this)
                    .title(getString(R.string.please_wait))
                    .content(getString(R.string.generating_new_wallet))
                    .progress(true, 0)
                    .cancelable(false)
                    .show();

            //Delete all wallets, generate and save new wallet
            val wm = WalletManager(this@CreateWalletActivity)
            doAsync({
                runOnUiThread {
                    dialog.dismiss()
                    toast(getString(R.string.wallet_generation_error) + ": " + it.message)
                    it.printStackTrace()
                }
            }) {
                val keyPair = wm.generatePrivateKey()!!
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
                runOnUiThread {
                    dialog.dismiss()
                    startActivity(MainActivity.newIntent(this@CreateWalletActivity))
                    finish()
                }
            }

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    startImportWalletActivity()

                } else {

                    toast(getString(R.string.permission_denied))

                }
                return
            }
            else -> {}
        }
    }

    private fun startImportWalletActivity() {
        startActivity(ImportWalletActivity.newIntent(this))
        finish()
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 0

        fun newIntent(context: Context): Intent {
            return Intent(context, CreateWalletActivity::class.java)
        }
    }
}
