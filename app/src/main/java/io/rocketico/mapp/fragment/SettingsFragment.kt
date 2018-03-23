package io.rocketico.mapp.fragment


import android.os.Bundle
import com.takisoft.fix.support.v7.preference.PreferenceFragmentCompat
import io.rocketico.mapp.R

class SettingsFragment : PreferenceFragmentCompat() {

    //todo implement logout
    private lateinit var fragmemtListener: OnSettingsItemClickListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fragmemtListener = activity as OnSettingsItemClickListener

        val preference = findPreference("export_private_key")
        preference.setOnPreferenceClickListener({
            fragmemtListener.onExportClick()
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
