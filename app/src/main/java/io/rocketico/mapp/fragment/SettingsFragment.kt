package io.rocketico.mapp.fragment


import android.os.Bundle
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import io.rocketico.mapp.R


class SettingsFragment : PreferenceFragmentCompat() {

    //todo implement logout
    //todo implement export private key via share

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings);
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}// Required empty public constructor
