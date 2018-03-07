package io.rocketico.core.model

import java.util.*

class Rate(val currency: String,
           val rates: List<TokenRate>,
           val timestamp: Long)

class TokenRate(val tokenSymbol: String,
                val rate: Float)