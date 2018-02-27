package io.rocketico.mapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import io.rocketico.mapp.R
import io.rocketico.mapp.adapter.TokenSendFlexibleItem
import io.rocketico.mapp.fragment.MenuFragment
import io.rocketico.mapp.fragment.MenuFragment.OnMenuButtonsClickListener
import io.rocketico.mapp.fragment.SendDetailsFragment
import io.rocketico.mapp.fragment.SendFragment

class MenuActivity : AppCompatActivity(),
        OnMenuButtonsClickListener,
        TokenSendFlexibleItem.OnItemClickListener,
        SendFragment.SendFragmentListener{

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

    override fun onClick(position: Int) {
        supportFragmentManager.beginTransaction()
                .replace(R.id.container, SendDetailsFragment())
                .addToBackStack(null)
                .commit()
    }
}
