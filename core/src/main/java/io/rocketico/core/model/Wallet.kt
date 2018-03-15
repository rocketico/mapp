package io.rocketico.core.model

import io.rocketico.core.WalletsPasswordManager
import org.web3j.crypto.Credentials
import java.io.Serializable
import java.util.*

class Wallet(
        private val _address: String,
        val publicKey: String,
        var name: String,
        var ethBalanceLastUpdate: Date? = Date(),
        var tokens: MutableList<Token>? = null,
        val uuid: UUID = UUID.randomUUID()
) : Serializable {
    val address: String
        get() {
            var result = _address
            if (_address.substring(0, 2) != "0x") {
                result = "0x" + _address
            }
            return result
        }
    var privateKey: String
        get() {
            val result = WalletsPasswordManager.getWalletPrivateKey(uuid)!!
            return result
        }
        set(value) {
            WalletsPasswordManager.saveWalletPrivateKey(uuid, value)
        }

    fun toCredentials(): Credentials? {
        return Credentials.create(privateKey)
    }
}