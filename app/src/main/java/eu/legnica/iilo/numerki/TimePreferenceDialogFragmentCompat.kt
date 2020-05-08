package eu.legnica.iilo.numerki

import android.os.Build
import android.os.Bundle
import android.text.format.DateFormat
import android.view.View
import android.widget.TimePicker
import androidx.preference.PreferenceDialogFragmentCompat
import java.lang.IllegalStateException

class TimePreferenceDialogFragmentCompat : PreferenceDialogFragmentCompat() {

    private lateinit var timePicker: TimePicker

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)

        timePicker = view.findViewById(R.id.edit)
            ?: throw IllegalStateException("Dialog view must contain a TimePicker with id 'edit'")

        // Get the time from the related Preference
        var minutesAfterMidnight: Int? = null
        val preference = preference
        if (preference is TimePreference) {
            minutesAfterMidnight = preference.time
        }

        // Set the time to the TimePicker
        if (minutesAfterMidnight != null) {
            val hours = minutesAfterMidnight / 60
            val minutes = minutesAfterMidnight % 60
            val is24hour = DateFormat.is24HourFormat(context)
            timePicker.setIs24HourView(is24hour)

            if(Build.VERSION.SDK_INT >= 23) {
                timePicker.hour = hours
                timePicker.minute = minutes
            } else {
                // `currentHour` jest oznaczone jako deprecated od wersji 23.
                // Mimo obecnego warunku Android Studio narzeka, że użycie jest niewłaściwe
                @Suppress("DEPRECATION")
                timePicker.currentHour = hours
                @Suppress("DEPRECATION")
                timePicker.currentMinute = minutes
            }
        }
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        if (positiveResult) { // generate value to save
            @Suppress("DEPRECATION")
            val hours = if(Build.VERSION.SDK_INT >= 23) timePicker.hour else timePicker.currentHour
            @Suppress("DEPRECATION")
            val minutes = if(Build.VERSION.SDK_INT >= 23) timePicker.minute else timePicker.currentMinute

            val minutesAfterMidnight = hours * 60 + minutes
            // Get the related Preference and save the value
            val preference = preference
            if (preference is TimePreference) {
                // This allows the client to ignore the user value.
                if (preference.callChangeListener(minutesAfterMidnight)) {
                    // Save the value
                    preference.time = minutesAfterMidnight
                }
            }
        }
    }

    companion object {
        fun newInstance(key: String?): TimePreferenceDialogFragmentCompat {
            val fragment = TimePreferenceDialogFragmentCompat()
            val bundle = Bundle(1)
            bundle.putString(ARG_KEY, key)
            fragment.arguments = bundle
            return fragment
        }
    }
}