package io.rocketico.mapp

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.view.WindowManager


object Utils {
    @SuppressLint("ObsoleteSdkInt")
    fun setStatusBarColor(activity: AppCompatActivity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }
}