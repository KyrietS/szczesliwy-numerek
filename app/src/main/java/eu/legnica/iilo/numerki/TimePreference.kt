package eu.legnica.iilo.numerki

import android.content.Context
import android.content.res.TypedArray
import android.util.AttributeSet
import androidx.preference.DialogPreference

class TimePreference @JvmOverloads constructor(
    context: Context?, attrs: AttributeSet? = null,
    defStyleAttr: Int = R.attr.dialogPreferenceStyle, defStyleRes: Int = defStyleAttr
) :
    DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    private var mTime = 0
    // Zapisz do Shared Preferences
    var time: Int
        get() = mTime
        set(time) {
            mTime = time
            // Zapisz do Shared Preferences
            persistInt(time)
            setSummary(R.string.check_time_summary)
            summary = summary.toString() + " " + getFormattedTime(time)
        }

    private fun getFormattedTime(time: Int): String {
        val hour = time / 60
        val minutes = time % 60
        val strHour = (if (hour < 10) "0" else "") + hour
        val strMinutes = (if (minutes < 10) "0" else "") + minutes
        return "$strHour:$strMinutes"
    }

    override fun onGetDefaultValue(a: TypedArray, index: Int): Any {
        // Default value from attribute. Fallback value is set to 0.
        return a.getInt(index, 0)
    }

    override fun onSetInitialValue(defaultValue: Any?) {
        val defaultTime = (defaultValue ?: 0) as Int
        time = getPersistedInt(defaultTime)
    }

    override fun getDialogLayoutResource(): Int {
        return R.layout.time_pref_dialog
    }
}