package io.rocketico.mapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.mapp.R

import kotlinx.android.synthetic.main.activity_sliding_up_menu.*

class SlidingUpMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding_up_menu)

        setupListeners()
    }

    private fun setupListeners() {
        closeButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }
}
