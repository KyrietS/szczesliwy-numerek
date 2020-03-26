package eu.legnica.iilo.numerki

import android.content.Context
import android.util.AttributeSet
import androidx.preference.MultiSelectListPreference

class ChooseNumbersPreference : MultiSelectListPreference {

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int)
            : super(context, attrs, defStyleAttr, defStyleRes)

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    @Suppress("unused")
    constructor(context: Context?, attrs: AttributeSet?)
            : super(context, attrs)

    @Suppress("unused")
    constructor(context: Context?)
            : super(context)

    override fun getSummary(): CharSequence {
        if (!isEnabled) {
            return super.getSummary()
        }

        val selectedItems = selectedItems
        val entries = entries
        val summary = context.resources.getString(R.string.choose_numbers_summary)
        val sb = StringBuilder()
        for (i in selectedItems.indices) {
            if (selectedItems[i]) {
                sb.append(entries[i].toString())
                sb.append(", ")
            }
        }
        if (sb.isNotEmpty()) {
            sb.deleteCharAt(sb.length - 1)
            sb.deleteCharAt(sb.length - 1)
        } else {
            sb.append(context.resources.getString(R.string.no_numbers_chosen))
        }
        return "$summary $sb"
    }
}