package com.fastaccess.ui.widgets.markdown

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import androidx.fragment.app.FragmentManager
import androidx.transition.TransitionManager
import com.fastaccess.R
import com.fastaccess.helper.AppHelper
import com.fastaccess.helper.InputHelper
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.provider.markdown.MarkDownProvider
import com.fastaccess.ui.modules.editor.emoji.EmojiBottomSheet
import com.fastaccess.ui.modules.editor.popup.EditorLinkImageDialogFragment
import com.fastaccess.utils.setOnThrottleClickListener
import com.google.android.material.snackbar.Snackbar

/**
 * Created by kosh on 11/08/2017.
 */
class MarkDownLayout : LinearLayout {
    private lateinit var editorIconsHolder: HorizontalScrollView
    private lateinit var addEmojiView: View

    private val sentFromOctoHub: String by lazy {
        "\n\n_" + resources.getString(R.string.sent_from_octohub, AppHelper.deviceName, "",
                "[" + resources.getString(R.string.app_name) + "](https://github.com/HardcodedCat/OctoHub/)") + "_"
    }

    var markdownListener: MarkdownListener? = null
    private var selectionIndex = 0

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)

    override fun onFinishInflate() {
        super.onFinishInflate()
        orientation = HORIZONTAL
        View.inflate(context, R.layout.markdown_buttons_layout, this)
        if (isInEditMode) return
        editorIconsHolder = findViewById(R.id.editorIconsHolder)
        addEmojiView = findViewById(R.id.addEmoji)
        findViewById<View>(R.id.view).setOnThrottleClickListener {
            onViewMarkDown()
        }
        listOf(
            R.id.headerOne, R.id.headerTwo,
            R.id.headerThree, R.id.bold,
            R.id.italic, R.id.strikethrough,
            R.id.bullet, R.id.header,
            R.id.code, R.id.numbered,
            R.id.quote, R.id.link,
            R.id.image,  R.id.unCheckbox,
            R.id.checkbox, R.id.inlineCode,
            R.id.addEmoji, R.id.signature
        ).map { findViewById<View>(it) }.setOnThrottleClickListener {
            onActions(it)
        }
    }

    override fun onDetachedFromWindow() {
        markdownListener = null
        super.onDetachedFromWindow()
    }

    private fun onViewMarkDown() {
        markdownListener?.let {
            it.getEditText().let { editText ->
                TransitionManager.beginDelayedTransition(this)
                if (editText.isEnabled && !InputHelper.isEmpty(editText)) {
                    editText.isEnabled = false
                    selectionIndex = editText.selectionEnd
                    MarkDownProvider.setMdText(editText, InputHelper.toString(editText))
                    editorIconsHolder.visibility = View.INVISIBLE
                    addEmojiView.visibility = View.INVISIBLE
                    ViewHelper.hideKeyboard(editText)
                } else {
                    editText.setText(it.getSavedText())
                    editText.setSelection(selectionIndex)
                    editText.isEnabled = true
                    editorIconsHolder.visibility = View.VISIBLE
                    addEmojiView.visibility = View.VISIBLE
                    ViewHelper.showKeyboard(editText)
                }
            }
        }
    }

    private fun onActions(v: View) {
        markdownListener?.let {
            it.getEditText().let { editText ->
                if (!editText.isEnabled) {
                    Snackbar.make(this, R.string.error_highlighting_editor, Snackbar.LENGTH_SHORT).show()
                } else {
                    when (v.id) {
                        R.id.link -> EditorLinkImageDialogFragment.newInstance(true, getSelectedText())
                            .show(it.fragmentManager(), "EditorLinkImageDialogFragment")
                        R.id.image -> EditorLinkImageDialogFragment.newInstance(false, getSelectedText())
                            .show(it.fragmentManager(), "EditorLinkImageDialogFragment")
                        R.id.addEmoji -> {
                            ViewHelper.hideKeyboard(it.getEditText())
                            EmojiBottomSheet().show(it.fragmentManager(), "EmojiBottomSheet")
                        }
                        else -> onActionClicked(editText, v.id)
                    }
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun onActionClicked(editText: EditText, id: Int) {
        if (editText.selectionEnd == -1 || editText.selectionStart == -1) {
            return
        }
        when (id) {
            R.id.headerOne -> MarkDownProvider.addHeader(editText, 1)
            R.id.headerTwo -> MarkDownProvider.addHeader(editText, 2)
            R.id.headerThree -> MarkDownProvider.addHeader(editText, 3)
            R.id.bold -> MarkDownProvider.addBold(editText)
            R.id.italic -> MarkDownProvider.addItalic(editText)
            R.id.strikethrough -> MarkDownProvider.addStrikeThrough(editText)
            R.id.numbered -> MarkDownProvider.addList(editText, "1.")
            R.id.bullet -> MarkDownProvider.addList(editText, "-")
            R.id.header -> MarkDownProvider.addDivider(editText)
            R.id.code -> MarkDownProvider.addCode(editText)
            R.id.quote -> MarkDownProvider.addQuote(editText)
            R.id.checkbox -> MarkDownProvider.addList(editText, "- [x]")
            R.id.unCheckbox -> MarkDownProvider.addList(editText, "- [ ]")
            R.id.inlineCode -> MarkDownProvider.addInlinleCode(editText)
            R.id.signature -> {
                markdownListener?.getEditText()?.let {
                    if (!it.text.toString().contains(sentFromOctoHub)) {
                        it.setText("${it.text}$sentFromOctoHub")
                    } else {
                        it.setText(it.text.toString().replace(sentFromOctoHub, ""))
                    }
                    editText.setSelection(it.text.length)
                    editText.requestFocus()
                }
            }
        }
    }

    fun onEmojiAdded(emoji: Emoji?) {
        markdownListener?.getEditText()?.let { editText ->
            ViewHelper.showKeyboard(editText)
            emoji?.let {
                MarkDownProvider.insertAtCursor(editText, ":${it.aliases[0]}:")
            }
        }
    }

    interface MarkdownListener {
        fun getEditText(): EditText
        fun fragmentManager(): FragmentManager
        fun getSavedText(): CharSequence?
    }

    fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markdownListener?.let {
            if (isLink) {
                MarkDownProvider.addLink(it.getEditText(), InputHelper.toString(title), InputHelper.toString(link))
            } else {
                MarkDownProvider.addPhoto(it.getEditText(), InputHelper.toString(title), InputHelper.toString(link))
            }
        }
    }

    private fun getSelectedText(): String? {
        markdownListener?.getEditText()?.let {
            if (it.text.toString().isNotBlank()) {
                val selectionStart = it.selectionStart
                val selectionEnd = it.selectionEnd
                return it.text.toString().substring(selectionStart, selectionEnd)
            }
        }
        return null
    }
}
