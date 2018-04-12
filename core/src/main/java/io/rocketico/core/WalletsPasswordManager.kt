package io.rocketico.core

import de.adorsys.android.securestoragelibrary.SecurePreferences
import java.util.*

object WalletsPasswordManager {
    private const val WALLETS_PASSWORDS_STORAGE_KEY_PREFIX = "wallet_n_"
    private const val WALLET_PASSWORD = "wallet_password_"

    fun saveWalletPrivateKey(walletUUID: UUID, privateKey: String) {
        SecurePreferences.setValue(WALLETS_PASSWORDS_STORAGE_KEY_PREFIX + walletUUID.toString(), privateKey)
    }

    fun getWalletPrivateKey(walletUUID: UUID): String? {
        return SecurePreferences.getStringValue(WALLETS_PASSWORDS_STORAGE_KEY_PREFIX + walletUUID.toString(), null)
    }

    fun deleteWalletPrivateKey(walletUUID: UUID) {
        SecurePreferences.removeValue(WALLETS_PASSWORDS_STORAGE_KEY_PREFIX + walletUUID.toString())
    }

    fun saveWalletPassword(walletUUID: UUID, password: String) {
        SecurePreferences.setValue(WALLET_PASSWORD + walletUUID.toString(), password)
    }

    fun getWalletPassword(walletUUID: UUID): String? {
        return SecurePreferences.getStringValue(WALLET_PASSWORD + walletUUID.toString(), null)
    }

    fun deleteWalletPassword(walletUUID: UUID) {
        SecurePreferences.removeValue(WALLET_PASSWORD + walletUUID.toString())
    }
}