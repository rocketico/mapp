package io.rocketico.core.model.response

import java.util.*

class TokenHistoryResponse {
    var tokenType: String? = null
    var addressFrom: String? = null
    var value: Float? = null
    var received: Boolean = false
    var confirmations: Long? = null
    var date: Date? = null
    var fee: Float? = null
}
