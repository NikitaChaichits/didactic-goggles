package com.example.vpn.util.view

import android.text.Editable
import android.text.TextWatcher
import android.text.method.DigitsKeyListener
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.annotation.StringRes
import androidx.core.text.HtmlCompat
import com.example.vpn.util.locale.currentDeviceLocale
import java.text.DecimalFormatSymbols
import java.util.*

fun TextView.setHtmlText(htmlString: String) {
    text = HtmlCompat.fromHtml(htmlString, HtmlCompat.FROM_HTML_MODE_LEGACY)
    movementMethod = LinkMovementMethod.getInstance()
}

fun TextView.setHtmlText(@StringRes htmlStringRes: Int) {
    val htmlString = resources.getString(htmlStringRes)
    setHtmlText(htmlString)
}

fun TextView.setupDecimalSeparator() {
    keyListener = DigitsKeyListener.getInstance("0123456789.,")
    val uiLocale = context.currentDeviceLocale()
    addTextChangedListener(DecimalInputTextWatcher(uiLocale))

}

class DecimalInputTextWatcher(
    uiLocale: Locale,
    private val decimalSeparatorsRegex: Regex = "[,.]".toRegex()
) : TextWatcher {

    /** Actual decimal separator according to the current uiLocale */
    private val decimalSeparator = DecimalFormatSymbols.getInstance(uiLocale).decimalSeparator

    private var originalInputValue: CharSequence? = null
    private var textDiff: TextDiff? = null

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
        originalInputValue = s.toString()
    }

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
        textDiff = TextDiff(originalInputValue!!, s.toString(), start, start + before, start + count)
        originalInputValue = null
    }

    override fun afterTextChanged(text: Editable) {
        textDiff?.let {
            if (it.originalInput.contains(decimalSeparator)) {
                if (it.insertion.contains(decimalSeparatorsRegex)) {
                    // already have the separator; omit the other ones
                    val updatedInsertion = it.insertion.replace(decimalSeparatorsRegex, "")
                    text.replace(it.start, it.after, updatedInsertion)
                }
                return@let
            }
            if (it.updatedInput.contains(decimalSeparatorsRegex)) {
                val resultedText = it.updatedInput.replace(decimalSeparatorsRegex, "$decimalSeparator")
                    // left only the last separator
                    .replace("\\$decimalSeparator(?=.*\\$decimalSeparator)".toRegex(), "")
                if (text.toString() != resultedText) {
                    text.replace(0, text.length, resultedText)
                }
            }
        }
        textDiff = null
    }

    data class TextDiff(
        val originalInput: CharSequence,
        val updatedInput: CharSequence,
        val start: Int,
        val before: Int,
        val after: Int
    ) {
        val insertion: CharSequence
            get() = updatedInput.subSequence(start, after)
    }
}
