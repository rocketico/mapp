package io.rocketico.core

import android.content.Context
import io.paperdb.Paper
import io.rocketico.core.model.Currency
import io.rocketico.core.model.TokenType
import io.rocketico.core.model.response.TokenRatesRangeResponse
import io.rocketico.core.model.response.TokensRatesResponse
import java.io.Serializable
import java.util.*

object RateHelper {
    private const val RATES_DB_KEY = "rates_db_key"
    private const val CURRENT_CURRENCY_KEY = "current_currency_key"

    fun getTokenRateByDate(date: Date = Date()): TokensRatesResponse? {
        return Utils.api.getRatesByDate(date).execute().body()
    }

    fun getTokenRatesByRange(from: Date, to: Date = Date()): TokenRatesRangeResponse? {
        return Utils.api.getRatesByDateRange(from, to).execute().body()
    }

    fun convertCurrency(rateFrom: Float, rateTo: Float, amount: Float): Float {
        return (rateFrom / rateTo) * amount
    }

    fun saveRates(context: Context, ratesEntity: RatesEntity) {
        Paper.init(context)
        Paper.book(RATES_DB_KEY).write(ratesEntity.currency.codeName, ratesEntity)
    }

    fun loadRates(context: Context, currency: Currency): RatesEntity {
        Paper.init(context)
        return Paper.book(RATES_DB_KEY).read<RatesEntity>(currency.codeName)
    }

    fun deleteRates(context: Context, currency: Currency) {
        Paper.init(context)
        Paper.book(RATES_DB_KEY).delete(currency.codeName)
    }

    fun existsRates(context: Context, currency: Currency): Boolean {
        Paper.init(context)
        return Paper.book(RATES_DB_KEY).contains(currency.codeName)
    }

    fun isOutdated(context: Context, currency: Currency): Boolean {
        //todo add check
        return !existsRates(context, currency)
    }

    fun getCurrentCurrency(context: Context): Currency {
        Paper.init(context)
        return Paper.book().read<Currency>(CURRENT_CURRENCY_KEY) ?: Currency.USD
    }

    fun setCurrentCurrency(context: Context, currentCurrency: Currency) {
        Paper.init(context)
        Paper.book().write(CURRENT_CURRENCY_KEY, currentCurrency)
    }

    fun getTokenRate(context: Context, tokenType: TokenType, currency: Currency): RatesEntity.Rate? {
        Paper.init(context)
        val tmp = Paper.book(RATES_DB_KEY).read<RatesEntity>(currency.codeName).rates
        return tmp.find { it.tokenType.codeName == tokenType.codeName }
    }

    class RatesEntity(val currency: Currency,
                      val rates: List<Rate>,
                      val date: Date) : Serializable {

        class Rate(val tokenType: TokenType,
                   val rate: Float) : Serializable

        companion object {
            fun parse(tokensRatesResponse: TokensRatesResponse): RatesEntity {
                val currencyTmp = Currency.currencyFromString(tokensRatesResponse.currency)
                val ratesTmp = mutableListOf<Rate>()

                tokensRatesResponse.rates.forEach {
                    val tokenType = it.tokenSymbol
                    val tmpType = TokenType.values().find {
                        it.codeName.toLowerCase() ==  tokenType.toLowerCase()
                    }
                    val tmpRate = it.rate

                    tmpType?.let { ratesTmp.add(Rate(tmpType, tmpRate)) }
                }

                return RatesEntity(currencyTmp!!, ratesTmp, tokensRatesResponse.date)
            }
        }
    }
}