package io.rocketico.core

import android.content.Context
import io.rocketico.core.model.Wallet
import io.paperdb.Paper
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger
import java.util.*

class WalletManager(val context: Context) {
    private val WALLETS_DATABASE = "wallets"

    fun generatePrivateKey(): ECKeyPair? {
        return Keys.createEcKeyPair()
    }

    fun publicKeyToAddress(publicKey: String): String? {
        return Keys.getAddress(publicKey)
    }

    fun privateKeyToPublicKey(privateKey: String): String? {
        return ECKeyPair.create(BigInteger(privateKey, 16)).publicKey.toString(16)
    }

    fun saveWallet(wallet: Wallet) {
        Paper.init(context)
        Paper.book(WALLETS_DATABASE).write(
                wallet.uuid.toString(),
                wallet
        )
    }

    fun getWallet(uuid: UUID, decryptPrivateKey: Boolean = false): Wallet? {
        Paper.init(context)
        return Paper.book(WALLETS_DATABASE).read<Wallet>(uuid.toString())
    }

    fun getWalletIdList(): List<UUID>? {
        Paper.init(context)
        val result: MutableList<UUID> = mutableListOf()
        Paper.book(WALLETS_DATABASE).allKeys.forEach {
            result.add(UUID.fromString(it))
        }
        return result
    }

    fun deleteWallet(uuid: UUID) {
        Paper.init(context)
        WalletsPasswordManager.deleteWalletPrivateKey(uuid)
        Paper.book(WALLETS_DATABASE).delete(uuid.toString())
    }

    fun existsWallet(uuid: UUID): Boolean {
        Paper.init(context)
        return Paper.book(WALLETS_DATABASE).contains(uuid.toString())
    }
}