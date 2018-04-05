package io.rocketico.mapp

import android.annotation.SuppressLint
import android.app.Fragment
import android.content.Context
import io.rocketico.core.RateHelper
import io.rocketico.core.model.TokenType
import org.jetbrains.anko.doAsyncResult

@SuppressLint("StringFormatMatches")
fun Context.setBalance(value: Float?): String {
    return value?.toString() ?: getString(R.string.null_value)
}

@SuppressLint("StringFormatMatches")
fun Context.setBalanceWithCurrency(value: Float?): String {
    val currentCurrency = RateHelper.getCurrentCurrency(this)
    return getString(R.string.balance_template, currentCurrency.currencySymbol,
            value ?: getString(R.string.null_value))
}

@SuppressLint("StringFormatMatches")
fun Context.setTokenBalance(tokenType: String, value: Float?): String {
    return getString(R.string.balance_template, tokenType, value ?: getString(R.string.null_value))
}

@SuppressLint("StringFormatMatches")
fun Context.setQuantity(prefix: String, value: Float?): String {
    return getString(R.string.quantity_template, prefix, value ?: getString(R.string.null_value))
}

fun <T, R> T.loadData(task: () -> R): R? =
        try {
            doAsyncResult { task() }.get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
