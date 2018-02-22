package io.rocketico.core

import de.adorsys.android.securestoragelibrary.SecurePreferences
import java.util.*

object WalletsPasswordManager {
    private const val WALLETS_PASSWORDS_STORAGE_KEY_PREFIX = "wallet_n_"

    fun saveWalletPrivateKey(walletUUID: UUID, privateKey: String) {
        SecurePreferences.setValue(WALLETS_PASSWORDS_STORAGE_KEY_PREFIX + walletUUID.toString(), privateKey)
    }

    fun getWalletPrivateKey(walletUUID: UUID): String? {
        return SecurePreferences.getStringValue(WALLETS_PASSWORDS_STORAGE_KEY_PREFIX + walletUUID.toString(), null)
    }

    fun deleteWalletPrivateKey(walletUUID: UUID) {
        SecurePreferences.removeValue(WALLETS_PASSWORDS_STORAGE_KEY_PREFIX + walletUUID.toString())
    }
}