package io.rocketico.core.model.response

import java.math.BigInteger

class WalletTokensResponse(val address: String,
                           val decimals: Int,
                           val name: String,
                           val symbol: String,
                           val balance: Float)