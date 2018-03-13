package io.rocketico.core.model.response

import java.util.*

class TokenHistoryResponse {
    var tokenType: String? = null
    var isReceived: Boolean = false
    var address: String? = null
    var fee: Float? = null
    var date: Date? = null
    var confirmations: Long? = null
    var value: Long? = null
}
