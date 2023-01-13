package `is`.xyz.mpv.config

import `is`.xyz.mpv.R
import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.Switch

class ReplaygainDialogPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.dialogPreferenceStyle,
        defStyleRes: Int = 0
): DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    private var replaygain_entries: Array<String>
    private var replaygain_values: Array<String>
    private var replaygain_source: String
    private var replaygain_preamp: String
    private var replaygain_fallback: String

    init {
        isPersistent = false
        dialogLayoutResource = R.layout.replaygain_pref

        // read list of scalers from specified resource
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ReplaygainPreferenceDialog)
        val res = styledAttrs.getResourceId(R.styleable.ReplaygainPreferenceDialog_replaygain_entries, -1)
        replaygain_entries = context.resources.getStringArray(res)
        val vals = styledAttrs.getResourceId(R.styleable.ReplaygainPreferenceDialog_replaygain_values, -1)
        replaygain_values = context.resources.getStringArray(vals)
        replaygain_source = styledAttrs.getString(R.styleable.ReplaygainPreferenceDialog_replaygain_source) ?: "no"
        replaygain_preamp = styledAttrs.getString(R.styleable.ReplaygainPreferenceDialog_replaygain_preamp) ?: "0.0"
        replaygain_fallback = styledAttrs.getString(R.styleable.ReplaygainPreferenceDialog_replaygain_fallback) ?: "0.0"

        styledAttrs.recycle()
    }

    private lateinit var myView: View

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        myView = view

        val s = myView.findViewById<Switch>(R.id.replaygain)
        val e1 = myView.findViewById<Spinner>(R.id.replaygain_source)
        val e2 = myView.findViewById<EditText>(R.id.replaygain_preamp)
        val e3 = myView.findViewById<EditText>(R.id.replaygain_fallback)

        s.isChecked = sharedPreferences.getBoolean(key, false)

        // populate Spinner and set selected item
        e1.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, replaygain_entries).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }

        if (!s.isChecked)
            e1.setSelection(0, false)
        else {
            val va = sharedPreferences.getString("${key}_source", "track")
            val idx = replaygain_values.indexOf(va)
            if (idx != -1)
                e1.setSelection(idx, false)
        }

        // populate EditText's
        e2.setText(sharedPreferences.getString("${key}_preamp", "0.0") ?: "0.0")
        e3.setText(sharedPreferences.getString("${key}_fallback", "0.0") ?: "0.0")
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        // save values only if user presses OK
        if (!positiveResult)
            return

        val s = myView.findViewById<Switch>(R.id.replaygain)
        val e1 = myView.findViewById<Spinner>(R.id.replaygain_source)
        val e2 = myView.findViewById<EditText>(R.id.replaygain_preamp)
        val e3 = myView.findViewById<EditText>(R.id.replaygain_fallback)

        val e = editor // Will create(!) a new SharedPreferences.Editor instance
        e.putBoolean(key, s.isChecked)
        if (!s.isChecked)
            e.putString("${key}_source", "no")
        else
            e.putString("${key}_source", replaygain_values[e1.selectedItemPosition])
        e.putString("${key}_preamp", e2.text.toString())
        e.putString("${key}_fallback", e3.text.toString())
        e.commit()
    }
}
