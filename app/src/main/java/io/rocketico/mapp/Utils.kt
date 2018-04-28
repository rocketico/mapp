package io.rocketico.mapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
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

    fun copyToClipboard(text: String, context: Context) {
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("", text)
        clipboard.primaryClip = clip
    }

    fun shareTextIntent(text: String): Intent {
        val sendIntent = Intent()
        sendIntent.action = Intent.ACTION_SEND
        sendIntent.putExtra(Intent.EXTRA_TEXT, text)
        sendIntent.type = "anim_test/plain"
        return sendIntent
    }

    fun isOnline(context: Context): Boolean {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val netInfo = cm.activeNetworkInfo
        return netInfo != null && netInfo.isConnectedOrConnecting
    }

    fun calculateDifference(new: Float?, old: Float?): Float? {
        val result = new?.let { old?.let { ((new * 100) / old) - 100 } }
        return result?.let { if (result.isNaN() || result.isInfinite()) null else result }
    }
}