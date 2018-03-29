package io.rocketico.mapp.fragment


import android.os.Bundle
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import io.rocketico.mapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    //todo implement logout
    private lateinit var fragmentListener: OnSettingsItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmentListener = activity as OnSettingsItemClickListener

        val preference = findPreference("export_private_key")
        preference.setOnPreferenceClickListener({
            fragmentListener.onExportClick()
            true
        })
    }

    override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
        addPreferencesFromResource(R.xml.settings)
    }

    interface OnSettingsItemClickListener {
        fun onExportClick()
    }

    companion object {
        fun newInstance(): SettingsFragment {
            return SettingsFragment()
        }
    }

}// Required empty public constructor
