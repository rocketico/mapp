package io.rocketico.mapp

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import io.rocketico.core.RateHelper
import org.jetbrains.anko.doAsyncResult

@SuppressLint("StringFormatMatches")
fun Context.setBalanceWithCurrency(value: Float): String {
    val currentCurrency = RateHelper.getCurrentCurrency(this)
    return getString(R.string.balance_template, currentCurrency.currencySymbol, value)
}

@SuppressLint("StringFormatMatches")
fun Context.setEthBalance(value: Float): String {
    return getString(R.string.balance_template, getString(R.string.ether_label), value)
}

fun <T, R> T.loadData(task: () -> R): R? =
        try {
            doAsyncResult { task() }.get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
