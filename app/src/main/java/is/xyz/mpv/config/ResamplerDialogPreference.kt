package `is`.xyz.mpv.config

import `is`.xyz.mpv.R
import android.content.Context
import android.preference.DialogPreference
import android.util.AttributeSet
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner

class ResamplerDialogPreference @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = android.R.attr.dialogPreferenceStyle,
        defStyleRes: Int = 0
): DialogPreference(context, attrs, defStyleAttr, defStyleRes) {
    private var resampler_entries: Array<String>
    private var resampler_values: Array<String>
    private var resampler_dithering_entries: Array<String>
    private var resampler_dithering_values: Array<String>
    private var resampler_cutoff: String
    private var resampler_dithering: String

    init {
        isPersistent = false
        dialogLayoutResource = R.layout.resampler_pref

        // read list of scalers from specified resource
        val styledAttrs = context.obtainStyledAttributes(attrs, R.styleable.ResamplerPreferenceDialog)
        val res = styledAttrs.getResourceId(R.styleable.ResamplerPreferenceDialog_resampler_entries, -1)
        resampler_entries = context.resources.getStringArray(res)
        val vals = styledAttrs.getResourceId(R.styleable.ResamplerPreferenceDialog_resampler_values, -1)
        resampler_values = context.resources.getStringArray(vals)
        val res_dither = styledAttrs.getResourceId(R.styleable.ResamplerPreferenceDialog_resampler_dithering_entries, -1)
        resampler_dithering_entries = context.resources.getStringArray(res_dither)
        val vals_dither = styledAttrs.getResourceId(R.styleable.ResamplerPreferenceDialog_resampler_dithering_values, -1)
        resampler_dithering_values = context.resources.getStringArray(vals_dither)
        resampler_cutoff = styledAttrs.getString(R.styleable.ResamplerPreferenceDialog_resampler_cutoff) ?: ",cutoff=0.97"
        resampler_dithering = styledAttrs.getString(R.styleable.ResamplerPreferenceDialog_resampler_dithering) ?: "0"

        styledAttrs.recycle()
    }

    private lateinit var myView: View

    override fun onBindDialogView(view: View) {
        super.onBindDialogView(view)
        myView = view

        val s = myView.findViewById<Spinner>(R.id.resampler)
        val e1 = myView.findViewById<EditText>(R.id.cutoff)
        val e2 = myView.findViewById<Spinner>(R.id.dithering)

        // populate Spinner and set selected item
        s.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, resampler_entries).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        e2.adapter = ArrayAdapter(context, android.R.layout.simple_spinner_item, resampler_dithering_entries).apply {
            setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        }
        val va = sharedPreferences.getString(key, "")
        val idx = resampler_values.indexOf(va)
        if (idx != -1)
            s.setSelection(idx, false)

        // populate EditText's
        e1.setText(sharedPreferences.getString("${key}_cutoff", ",cutoff=0.97").substring(8))

        val va2 = sharedPreferences.getString("${key}_dithering", "")
        val idx2 = resampler_dithering_values.indexOf(va2)
        if (idx2 != -1)
            e2.setSelection(idx2, false)
    }

    override fun onDialogClosed(positiveResult: Boolean) {
        super.onDialogClosed(positiveResult)

        // save values only if user presses OK
        if (!positiveResult)
            return

        val s = myView.findViewById<Spinner>(R.id.resampler)
        val e1 = myView.findViewById<EditText>(R.id.cutoff)
        val e2 = myView.findViewById<Spinner>(R.id.dithering)

        val e = editor // Will create(!) a new SharedPreferences.Editor instance
        e.putString(key, resampler_values[s.selectedItemPosition])
        e.putString("${key}_cutoff", ",cutoff=" + e1.text.toString())
        e.putString("${key}_dithering", resampler_dithering_values[e2.selectedItemPosition])
        e.commit()
    }
}
