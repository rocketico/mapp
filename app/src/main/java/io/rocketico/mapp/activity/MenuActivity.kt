package io.rocketico.mapp.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import io.rocketico.core.WalletManager
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.R
import io.rocketico.mapp.Utils
import io.rocketico.mapp.fragment.*
import io.rocketico.mapp.fragment.MenuFragment.OnMenuButtonsClickListener
import org.jetbrains.anko.toast

class MenuActivity : AppCompatActivity(),
        OnMenuButtonsClickListener,
        SendFragment.SendFragmentListener,
        SendDetailsFragment.SendDetailsFragmentListener,
        SendBillFragment.SendBillFragmentListener {

    private lateinit var wallet: Wallet
    private var action: Int = NO_ACTION

    override fun onSettingsClick() {
        startActivity(SettingsActivity.getIntent(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        wallet = WalletManager(this).getWallet()!!

        action = intent.getIntExtra(ACTION, NO_ACTION)
        when(action) {
            ACTION_SENT -> {
                val tokenType = intent.getSerializableExtra(TOKEN_TYPE) as TokenType
                onSendTokenListItemClick(tokenType, null)
            }
            ACTION_RECEIVE -> { onReceiveClick() }
            NO_ACTION -> { init() }
        }
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MenuFragment.newInstance())
                .commit()
    }

    override fun onSendClick() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA),
                    CAMERA_PERMISSION_REQUEST)

        } else {
            startSendFragment()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {

                    startSendFragment()

                } else {

                    toast(getString(R.string.permission_denied))

                }
                return
            }
            else -> {}
        }
    }

    private fun startSendFragment() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendFragment.newInstance(wallet))
                .commit()
    }

    override fun onReceiveClick() {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, ReceiveFragment.newInstance(wallet))
        if (action == NO_ACTION) transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackClick() {
        Utils.setStatusBarColor(this, resources.getColor(R.color.colorPrimaryDark))
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        if (action == NO_ACTION) overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }

    override fun onSendTokenListItemClick(tokenType: TokenType, address: String?) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container, SendDetailsFragment.newInstance(tokenType, address))
        if (action == NO_ACTION) transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onCreateClick(tokenType: TokenType, eth: Float, gasPrice: Int, address: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendBillFragment.newInstance(wallet, tokenType, eth, gasPrice, address))
                .addToBackStack(null)
                .commit()
        Utils.setStatusBarColor(this, resources.getColor(R.color.white))
    }

    override fun onCloseClick() {
        finish()
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }

    override fun onLogInClick() {
        startActivity(LogInActivity.newIntent(this, LogInActivity.LOG_IN_FRAGMENT))
    }

    override fun onJoinClick() {
        startActivity(LogInActivity.newIntent(this, LogInActivity.JOIN_FRAGMENT))
    }

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 0
        private const val ACTION = "action"
        private const val TOKEN_TYPE = "token_type"
        const val NO_ACTION = -1
        const val ACTION_SENT = 1
        const val ACTION_RECEIVE = 2

        fun newIntent(context: Context, action: Int? = null, tokenType: TokenType? = null): Intent {
            val intent = Intent(context, MenuActivity::class.java)
            action?.let { intent.putExtra(ACTION, it) }
            tokenType?.let { intent.putExtra(TOKEN_TYPE, tokenType) }
            return intent
        }
    }
}
