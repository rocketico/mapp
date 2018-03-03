package io.rocketico.mapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenSendFlexibleItem
import io.rocketico.mapp.fragment.MenuFragment
import io.rocketico.mapp.fragment.MenuFragment.OnMenuButtonsClickListener
import io.rocketico.mapp.fragment.SendBillFragment
import io.rocketico.mapp.fragment.SendDetailsFragment
import io.rocketico.mapp.fragment.SendFragment

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
                .replace(R.id.container, MenuFragment())
                .commit()
    }

    override fun onSendClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendFragment())
                .commit()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }

    override fun onClick(position: Int) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendDetailsFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendBillFragment())
                .addToBackStack(null)
                .commit()
    }

    override fun onCloseClick() {
        finish()
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }
}
