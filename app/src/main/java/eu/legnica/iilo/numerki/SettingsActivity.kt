package eu.legnica.iilo.numerki

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.preference.PreferenceFragmentCompat

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, PreferenceFragmentCustom())
            .commit()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        val root = findViewById<LinearLayout>(R.id.linear_layout)
        val toolbar = LayoutInflater.from(this)
            .inflate(R.layout.settings_toolbar, root, false) as Toolbar
        root.addView(toolbar, 0) // insert at top
        toolbar.setNavigationOnClickListener { finish() }
    }

    @Suppress("unused")
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle, rootKey: String) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
        }
    }

    @Suppress("UNUSED_PARAMETER")
    fun onAboutClick(view: View?) {
        @SuppressLint("InflateParams")
        val aboutView: View = layoutInflater.inflate(R.layout.about, null, false)
        val version = aboutView.findViewById<TextView>(R.id.version)
        version.text = getString(R.string.about_version, BuildConfig.VERSION_NAME)
        val builder = AlertDialog.Builder(this)
        builder.setIcon(R.drawable.ic_help_24px)
        builder.setTitle(getString(R.string.about_title))
        builder.setView(aboutView)
        builder.setPositiveButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.create()
        builder.show()
    }
}