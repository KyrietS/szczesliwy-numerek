package eu.legnica.iilo.numerki

import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.preference.Preference
import androidx.preference.PreferenceCategory
import androidx.preference.PreferenceFragmentCompat

class PreferenceFragmentCustom : PreferenceFragmentCompat() {
    private var notifyNumbersPreference: Preference? = null

    override fun onCreatePreferences(bundle: Bundle?, s: String?) {
        addPreferencesFromResource(R.xml.root_preferences)
        notifyNumbersPreference = findPreference(getString(R.string.notify_numbers_key))
        // Zaktualizuj status opcji z wyborem numerka
        updateUserType(null)
    }

    override fun onDisplayPreferenceDialog(preference: Preference) {
        var dialogFragment: DialogFragment? = null
        // ---- Custom Preferences ----
        // Sprawdź czy wybrano opcję z wyborem godziny
        if (preference is TimePreference) {
            dialogFragment = TimePreferenceDialogFragmentCompat.newInstance(preference.getKey())
        }
        // ----------------------------
        // Utworzono jeden z customowych dialogów. Wyświetl go.
        if (dialogFragment != null && this.fragmentManager != null) {
            dialogFragment.setTargetFragment(this, 0)
            dialogFragment.show(
                this.fragmentManager!!, "androidx.preference" +
                        ".PreferenceFragment.DIALOG"
            )
        } else { // Wybrano standardowy dialog. Uruchamiamy go normalnie.
            super.onDisplayPreferenceDialog(preference)
        }
        // Zmiana typu użytkownika ma wpływ na inne ustawienia.
        if (preference.key == getString(R.string.user_type_key)) {
            preference.onPreferenceChangeListener =
                Preference.OnPreferenceChangeListener { _: Preference?, newValue: Any ->
                    updateUserType(newValue.toString())
                    true
                }
        }
    }

    // Zmiana typu użytkownika ma wpływ na inne ustawienia.
    private fun updateUserType(newUserType: String?) {
        val preferenceCategory = findPreference<PreferenceCategory>(getString(R.string.notify_category_key))!!
        val notifyPreference = findPreference<Preference>(getString(R.string.notify_key))!!
        val prefs = preferenceManager.sharedPreferences
        val userTypeName = newUserType ?: prefs.getString(getString(R.string.user_type_key), "unknown")

        if (userTypeName == "teacher") {
            preferenceCategory.removePreference(notifyNumbersPreference)
            notifyPreference.setTitle(R.string.notify_teacher)
        } else {
            preferenceCategory.addPreference(notifyNumbersPreference)
            notifyPreference.setTitle(R.string.notify_student)
        }
    }
}