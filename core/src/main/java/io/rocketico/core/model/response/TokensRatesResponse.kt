package io.rocketico.core.model.response

import java.util.*

class TokensRatesResponse(val currency: String,
                          val rates: List<Rate>,
                          val date: Date) {

    class Rate(val tokenSymbol: String,
               val rate: Float)
}