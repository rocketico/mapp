package io.rocketico.mapp.activity

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import io.rocketico.core.model.Token
import io.rocketico.core.model.TokenType
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenSendFlexibleItem
import io.rocketico.mapp.fragment.*
import io.rocketico.mapp.fragment.MenuFragment.OnMenuButtonsClickListener

class MenuActivity : AppCompatActivity(),
        OnMenuButtonsClickListener,
        TokenSendFlexibleItem.OnItemClickListener,
        SendFragment.SendFragmentListener,
        SendDetailsFragment.SendDetailsFragmentListener,
        SendBillFragment.SendBillFragmentListener {

    override fun onSettingsClick() {
        startActivity(SettingsActivity.getIntent(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        init()
    }

    private fun init() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, MenuFragment.newInstance())
                .commit()
    }

    override fun onSendClick() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), 0);
        } else {
            supportFragmentManager.beginTransaction()
                    .replace(R.id.container, SendFragment.newInstance())
                    .commit()
        }
    }

    override fun onReceiveClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, ReceiveFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }

    override fun onItemClick(tokenType: TokenType) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendDetailsFragment.newInstance(tokenType))
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateClick(tokenType: TokenType, eth: Float, address: String) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendBillFragment.newInstance(tokenType, eth, address))
                .addToBackStack(null)
                .commit()
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
}
