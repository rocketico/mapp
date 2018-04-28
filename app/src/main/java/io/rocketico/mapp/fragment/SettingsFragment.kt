package io.rocketico.mapp.fragment


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.preference.SwitchPreferenceCompat
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import io.rocketico.core.WalletManager
import io.rocketico.core.WalletsPasswordManager
import io.rocketico.core.model.Wallet
import io.rocketico.mapp.Cc
import io.rocketico.mapp.R
import io.rocketico.mapp.activity.SecurityActivity

class SettingsFragment : PreferenceFragmentCompat() {

    private lateinit var fragmentListener: OnSettingsItemClickListener
    private lateinit var isPasswordEnabledPreference: SwitchPreferenceCompat
    private lateinit var wallet: Wallet

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as OnSettingsItemClickListener

        wallet = WalletManager(context!!).getWallet()!!
        val password = WalletsPasswordManager.getWalletPassword(wallet.uuid)

        val exportPrivateKey = findPreference("export_private_key")
        exportPrivateKey.setOnPreferenceClickListener({
            fragmentListener.onExportClick()
            true
        })

        isPasswordEnabledPreference = findPreference("password_key") as SwitchPreferenceCompat
        isPasswordEnabledPreference.isChecked = password != null

        isPasswordEnabledPreference.setOnPreferenceChangeListener { _, newValue ->
            val value = newValue as Boolean
            if (value) {
                startActivityForResult(SecurityActivity.newIntent(context!!, SecurityActivity.ADD_PASSWORD_CODE), Cc.ADD_PASSWORD_REQUEST)
            } else {
                WalletsPasswordManager.deleteWalletPassword(wallet.uuid)
            }
            true
        }

        val logout = findPreference("logout")
        logout.setOnPreferenceClickListener {
            fragmentListener.onLogoutClick()
            true
        }
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        when (requestCode) {
            Cc.ADD_PASSWORD_REQUEST -> {
                if (resultCode != Activity.RESULT_OK) {
                    isPasswordEnabledPreference.isChecked = false
                }
            }
        }
    }

    interface OnSettingsItemClickListener {
        fun onExportClick()
        fun onLogoutClick()
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}// Required empty public constructor
