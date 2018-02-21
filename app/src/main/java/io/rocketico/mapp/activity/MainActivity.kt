package io.rocketico.mapp.activity

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener
import io.rocketico.mapp.R
import io.rocketico.mapp.fragment.WalletListFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.header_main.*
import kotlinx.android.synthetic.main.sliding_menu.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        setupListeners()

//        supportFragmentManager.beginTransaction()
//                .replace(R.id.fragmentContainer, WalletListFragment.newInstance(listOf()))
//                .commit()
    }

    private fun init() {
        supportActionBar?.setDisplayShowTitleEnabled(false)

        slidingLayout.isTouchEnabled = false
    }

    private fun setupListeners() {
        slidingLayout.addPanelSlideListener(object : PanelSlideListener {
            override fun onPanelSlide(panel: View?, slideOffset: Float) {
                Log.i(SLIDER_TAG, "onPanelSlide, offset " + slideOffset)
            }

            override fun onPanelStateChanged(panel: View?, previousState: PanelState?, newState: PanelState?) {
                Log.i(SLIDER_TAG, "onPanelStateChanged " + newState)
            }

        })

        menuImageButton.setOnClickListener {
            if (slidingLayout.panelState != PanelState.COLLAPSED) {
                slidingLayout.panelState = PanelState.COLLAPSED
            } else {
                slidingLayout.panelState = PanelState.EXPANDED
            }
        }

        closeMenuButton.setOnClickListener {
            slidingLayout.panelState = PanelState.COLLAPSED
        }
    }

    override fun onBackPressed() {
        if (slidingLayout.panelState == PanelState.EXPANDED) {
            slidingLayout.panelState = PanelState.COLLAPSED
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val SLIDER_TAG = "SliderLayout"
    }
}
