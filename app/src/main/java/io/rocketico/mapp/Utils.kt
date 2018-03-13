package io.rocketico.mapp

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.WindowManager
import java.util.*


object Utils {
    @SuppressLint("ObsoleteSdkInt")
    fun setStatusBarColor(activity: Activity, color: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = activity.window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = color
        }
    }

    fun yesterday(): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -1)
        return cal.getTime()
    }

    fun nDaysAgo(days: Int): Date {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DATE, -days)
        return cal.getTime()
    }
}