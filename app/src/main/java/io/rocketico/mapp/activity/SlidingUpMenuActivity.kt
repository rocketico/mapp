package io.rocketico.mapp.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent
import android.view.View
import io.rocketico.mapp.R

import kotlinx.android.synthetic.main.activity_sliding_up_menu.*

class SlidingUpMenuActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sliding_up_menu)

        setupListeners()
    }

    private fun setupListeners() {
        closeButton.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                onBackPressed()
                true
            } else false
        }

        //TODO for testing
        sendButton.setOnClickListener {
            if (loggedOutView.visibility != View.GONE) {
                loggedOutView.visibility = View.GONE
                loggedInView.visibility = View.VISIBLE
            } else {
                loggedOutView.visibility = View.VISIBLE
                loggedInView.visibility = View.GONE
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.anim_slide_down, R.anim.anim_stand)
    }
}
