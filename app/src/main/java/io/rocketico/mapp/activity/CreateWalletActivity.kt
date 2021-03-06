package io.rocketico.mapp.activity

import android.Manifest
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.DisplayMetrics
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import com.afollestad.materialdialogs.MaterialDialog
import io.rocketico.core.WalletManager
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import kotlinx.android.synthetic.main.activity_create_wallet.*
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

class CreateWalletActivity : BaseSecureActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_wallet)

        init()
        makeBalloonsFly()
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

    private fun makeBalloonsFly() {
        setupAnimation(balloon1, 150000, 1, 1f)
        setupAnimation(balloon2, 120000, 1, 3f)
        setupAnimation(balloon3, 90000, 1, 4f)
        setupAnimation(balloon4, 60000, 1, 1f)
    }

    private fun setupAnimation(view: ImageView, duration: Long, direction: Int, startPosition: Float? = null) {
        val displayMetrics = DisplayMetrics()
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val screenWidth = displayMetrics.widthPixels.toFloat()
        val imageWidth = view.drawable.intrinsicWidth.toFloat()

        val from = startPosition?.let { startPosition / 5f * screenWidth }
                ?: if (direction == 1) imageWidth * (-1) else screenWidth
        val to = if (direction == -1) imageWidth * (-1) else screenWidth

        val animation = ObjectAnimator.ofFloat(view, View.X, from, to)
        animation.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {}

            override fun onAnimationEnd(animation: Animator?) {
                setupAnimation(view, duration, direction)
            }

            override fun onAnimationCancel(animation: Animator?) {}

            override fun onAnimationStart(animation: Animator?) {}
        })
        animation.interpolator = LinearInterpolator()
        if (startPosition == null) animation.repeatCount = ObjectAnimator.INFINITE
        val tmp = startPosition?.let { if (direction == 1) it else 5 - it } ?: 0f
        animation.duration = duration - (duration * tmp / 5).toLong()
        animation.start()
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
            else -> {
            }
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
