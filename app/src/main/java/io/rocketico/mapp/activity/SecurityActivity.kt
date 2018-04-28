package io.rocketico.mapp.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import io.rocketico.core.WalletManager
import io.rocketico.core.WalletsPasswordManager
import io.rocketico.mapp.R
import io.rocketico.mapp.fragment.AddPasswordFragment
import io.rocketico.mapp.fragment.FingerprintFragment
import io.rocketico.mapp.fragment.PasswordFragment

class SecurityActivity : AppCompatActivity(),
        FingerprintFragment.FingerprintFragmentListener,
        PasswordFragment.PasswordFragmentListener,
        AddPasswordFragment.AddPasswordFragmentListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint)

        val fragmentCode = intent.getIntExtra(FRAGMENT_CODE, -1)
        when (fragmentCode) {
            FINGERPRINT_CODE -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fingerprintContainer, FingerprintFragment.newInstance())
                        .commit()
            }
            PASSWORD_CODE -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fingerprintContainer, PasswordFragment.newInstance())
                        .commit()
            }
            ADD_PASSWORD_CODE -> {
                supportFragmentManager.beginTransaction()
                        .replace(R.id.fingerprintContainer, AddPasswordFragment.newInstance())
                        .commit()
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun isFingerprintOK() {
        setResult(Activity.RESULT_OK)
        finish()
    }

    override fun onBackClick() {
        onBackPressed()
    }

    override fun onEnterPasswordClick() {
        supportFragmentManager.beginTransaction()
                .replace(R.id.fingerprintContainer, PasswordFragment.newInstance())
                .addToBackStack(null)
                .commit()
    }

    override fun onCreateClick(password: String) {
        WalletsPasswordManager.saveWalletPassword(WalletManager(this).getWallet()?.uuid!!, password)
        setResult(Activity.RESULT_OK)
        finish()
    }

    companion object {
        private const val FRAGMENT_CODE = "fragment_code"
        const val FINGERPRINT_CODE = 1
        const val PASSWORD_CODE = 2
        const val ADD_PASSWORD_CODE = 3

        fun newIntent(context: Context, fragmentCode: Int): Intent {
            val intent = Intent(context, SecurityActivity::class.java)
            intent.putExtra(FRAGMENT_CODE, fragmentCode)
            return intent
        }
    }
}