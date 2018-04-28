package io.rocketico.mapp

import android.annotation.SuppressLint
import android.content.Context
import io.rocketico.core.RateHelper
import org.jetbrains.anko.doAsyncResult
import java.io.InputStream
import kotlin.math.absoluteValue

@SuppressLint("StringFormatMatches")
fun Context.setBalance(value: Float?, scale: Int? = null): String {
    var result: String = value?.toString() ?: getString(R.string.null_value)
    scale?.let {
        value?.let {
            result = String.format("%.${scale}f", value.toFloat()).replace(',', '.')
        }
    }
    return result
}

fun Context.setNullBalance(): String {
    return setBalance(null)
}

@SuppressLint("StringFormatMatches")
fun Context.setBalanceWithCurrency(value: Float?, scale: Int? = null): String {
    val currentCurrency = RateHelper.getCurrentCurrency(this)
    var result: String = value?.toString() ?: getString(R.string.null_value)
    scale?.let {
        value?.let {
            result = String.format("%.${scale}f", value.toFloat()).replace(',', '.')
        }
    }
    return getString(R.string.balance_template, currentCurrency.currencySymbol, result)
}

@SuppressLint("StringFormatMatches")
fun Context.setTokenBalance(tokenType: String, value: Float?, scale: Int? = null): String {
    var result: String = value?.toString() ?: getString(R.string.null_value)
    scale?.let {
        value?.let {
            result = String.format("%.${scale}f", value.toFloat()).replace(',', '.')
        }
    }
    return getString(R.string.balance_template, tokenType, result)
}

@SuppressLint("StringFormatMatches")
fun Context.setQuantity(prefix: String, value: Float?, scale: Int? = null): String {
    var result: String = value?.toString() ?: getString(R.string.null_value)
    scale?.let {
        value?.let {
            result = String.format("%.${scale}f", value.toFloat()).replace(',', '.')
        }
    }
    return getString(R.string.quantity_template, prefix, result)
}

@SuppressLint("StringFormatMatches")
fun Context.setFee(prefix: String, value: Float?): String {
    return getString(R.string.balance_template, value ?: getString(R.string.null_value), prefix)
}

fun Context.loadIcon(tokenCodeName: String): InputStream? {
    return try {
        assets.open("tokens_icons/${tokenCodeName}.webp")
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun Context.setRateDifference(diff: Float?): String {
    val strDiff = diff?.let { String.format("%.2f", it.absoluteValue).replace(",", ".") }
            ?: getString(R.string.null_value)
    return getString(R.string.diff_template, strDiff) + "%"
}

fun <T, R> T.loadData(task: () -> R): R? =
        try {
            doAsyncResult { task() }.get()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
