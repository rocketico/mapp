package io.rocketico.core

import android.content.Context
import io.paperdb.Paper
import io.rocketico.core.model.Wallet
import org.web3j.crypto.ECKeyPair
import org.web3j.crypto.Keys
import java.math.BigInteger

class WalletManager(val context: Context) {
    private val WALLETS_DATABASE = "wallets"
    private val WALLET_KEY = "main_wallet"

    fun generatePrivateKey(): ECKeyPair? {
        //return ECKeyPair.create(BigInteger("90653bcaf8bea8d07258ba9191fb0a2496245f916fe8e4b6044017283d15054e", 16)) //todo debug. remove me
        return Keys.createEcKeyPair()
    }

    fun privateKeyToKeyPair(privateKey: String): ECKeyPair? {
        return ECKeyPair.create(BigInteger(privateKey, 16))
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
                WALLET_KEY,
                wallet
        )
    }

    fun getWallet(): Wallet? {
        Paper.init(context)
        return Paper.book(WALLETS_DATABASE).read<Wallet>(WALLET_KEY)
    }

    fun deleteWallet() {
        Paper.init(context)
        WalletsPasswordManager.deleteWalletPrivateKey(getWallet()?.uuid!!)
        Paper.book(WALLETS_DATABASE).delete(WALLET_KEY)
    }

    fun existsWallet(): Boolean {
        Paper.init(context)
        return Paper.book(WALLETS_DATABASE).contains(WALLET_KEY)
    }
}