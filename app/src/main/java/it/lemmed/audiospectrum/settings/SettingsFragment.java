package it.lemmed.audiospectrum.settings;

import android.os.Bundle;
import android.widget.Toast;

import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SeekBarPreference;
import androidx.preference.SwitchPreferenceCompat;

import it.lemmed.audiospectrum.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    //FIELDS
    private final static String TAG = SettingsFragment.class.getName();
    public final static String SETTINGS_SHARED_PREFERENCES_FILE_NAME = TAG + ".SETTINGS_SHARED_PREFERENCES_FILE_NAME";

    //METHODS
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Define the settings file to use by this settings fragment
        getPreferenceManager().setSharedPreferencesName(SETTINGS_SHARED_PREFERENCES_FILE_NAME);
        setPreferencesFromResource(R.xml.root_preferences, rootKey);

        SwitchPreferenceCompat switchPref1 = findPreference("key_fft_graphic");
        if (switchPref1.isChecked()) {
            switchPref1.setSummary(R.string.list_pref06_entry01);
        }
        else {
            switchPref1.setSummary(R.string.list_pref06_entry02);
        }
        switchPref1.setOnPreferenceChangeListener((preference, newValue) -> {
            if (((boolean) newValue)) {
                switchPref1.setSummary(R.string.list_pref06_entry01);
            }
            else {
                switchPref1.setSummary(R.string.list_pref06_entry02);
            }
            return true;
        });

        EditTextPreference editText = findPreference("key_visualization_rate");
        editText.setOnPreferenceChangeListener((preference, newValue) -> {
            int parsedInt = -1;
            try {
                parsedInt = Integer.parseInt((String) newValue);
            }
            catch (NumberFormatException e) {
                Toast.makeText(editText.getContext(), getResources().getString(R.string.edit_text01_err), Toast.LENGTH_SHORT).show();
                return false;
            }
            if (parsedInt <= 20000) {
                editText.setText(Integer.toString(parsedInt));
                return true;
            }
            else {
                Toast.makeText(editText.getContext(), getResources().getString(R.string.edit_text01_err), Toast.LENGTH_SHORT).show();
                return false;
            }
        });
    }
}
