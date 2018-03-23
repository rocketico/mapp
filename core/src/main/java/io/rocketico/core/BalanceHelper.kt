package io.rocketico.core

import android.content.Context
import io.paperdb.Paper
import io.rocketico.core.model.TokenType
import java.math.BigInteger

object BalanceHelper {
    private const val BALANCE_DB_KEY = "balance_db_key"

    fun saveTokenBalance(context: Context, tokenType: TokenType, value: BigInteger) {
        Paper.init(context)
        Paper.book(BALANCE_DB_KEY).write(tokenType.codeName, value)
    }

    fun loadTokenBalance(context: Context, tokenType: TokenType): BigInteger? {
        Paper.init(context)
        return Paper.book(BALANCE_DB_KEY).read<BigInteger>(tokenType.codeName)
    }

    fun exists(context: Context, tokenType: TokenType): Boolean {
        Paper.init(context)
        return Paper.book(BALANCE_DB_KEY).contains(tokenType.codeName)
    }

    fun isTokenBalanceOutdated(context: Context, tokenType: TokenType): Boolean {
        Paper.init(context)
        if (!exists(context, tokenType)) return true
        val currentTime = System.currentTimeMillis()
        val differenceTime = currentTime - Paper.book(BALANCE_DB_KEY).lastModified(tokenType.codeName)
        return differenceTime > Cc.timeout
    }

    //force make token balance outdated
    fun outDateBalance(context: Context, tokenType: TokenType) {
        Paper.init(context)
        Paper.book(BALANCE_DB_KEY).delete(tokenType.codeName)
    }
}