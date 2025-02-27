package com.fastaccess.ui.modules.editor

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import androidx.annotation.StringRes
import androidx.fragment.app.FragmentManager
import androidx.transition.TransitionManager
import com.evernote.android.state.State
import com.fastaccess.R
import com.fastaccess.data.dao.EditReviewCommentModel
import com.fastaccess.data.entity.Comment
import com.fastaccess.helper.*
import com.fastaccess.provider.emoji.Emoji
import com.fastaccess.provider.markdown.MarkDownProvider
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.markdown.MarkDownLayout
import com.fastaccess.ui.widgets.markdown.MarkdownEditText
import com.fastaccess.utils.setOnThrottleClickListener

/**
 * Created by Kosh on 27 Nov 2016, 1:32 AM
 */

class EditorActivity : BaseActivity<EditorMvp.View, EditorPresenter>(), EditorMvp.View {
    private var participants: ArrayList<String>? = null
    private val sentFromOctoHub: String by lazy {
        "\n\n_" + getString(R.string.sent_from_octohub, AppHelper.deviceName, "",
                "[" + getString(R.string.app_name) + "](https://github.com/HardcodedCat/OctoHub/)") + "_"
    }

    private val replyQuote: LinearLayout by lazy { viewFind(R.id.replyQuote)!! }
    val quote: FontTextView by lazy { viewFind(R.id.replyQuoteText)!! }
    val markDownLayout: MarkDownLayout by lazy { viewFind(R.id.markDownLayout)!! }
    val editText: MarkdownEditText by lazy { viewFind(R.id.editText)!! }
    private val listDivider: View by lazy { viewFind(R.id.list_divider)!! }
    val parentView: View by lazy { viewFind(R.id.parentView)!! }
    val mention: ListView by lazy { viewFind(R.id.autocomplete)!! }

    @State
    @BundleConstant.ExtraType
    var extraType: String? = null
    @State
    var itemId: String? = null
    @State
    var login: String? = null
    @State
    var issueNumber: Int = 0
    @State
    var commentId: Long = 0
    @State
    var sha: String? = null
    @State
    var reviewComment: EditReviewCommentModel? = null

    override fun layout(): Int = R.layout.editor_layout

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): EditorPresenter = EditorPresenter()

    private fun onToggleQuote() {
        TransitionManager.beginDelayedTransition((parentView as ViewGroup))
        if (quote.maxLines == 3) {
            quote.maxLines = Integer.MAX_VALUE
        } else {
            quote.maxLines = 3
        }
        quote.setCompoundDrawablesWithIntrinsicBounds(
            0, 0,
            if (quote.maxLines == 3) R.drawable.ic_arrow_drop_down
            else R.drawable.ic_arrow_drop_up, 0
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        quote.setOnThrottleClickListener {
            onToggleQuote()
        }
        markDownLayout.markdownListener = this
        setToolbarIcon(R.drawable.ic_clear)
        if (savedInstanceState == null) {
            onCreate()
        }
        invalidateOptionsMenu()
        editText.initListView(mention, listDivider, participants)
        editText.requestFocus()
    }

    override fun onSendResultAndFinish(commentModel: Comment, isNew: Boolean) {
        hideProgress()
        val intent = Intent()
        intent.putExtras(
            Bundler.start()
                .put(BundleConstant.ITEM, commentModel)
                .put(BundleConstant.EXTRA, isNew)
                .end()
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onSendMarkDownResult() {
        val intent = Intent()
        intent.putExtras(Bundler.start().put(BundleConstant.EXTRA, editText.savedText).end())
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onSendReviewResultAndFinish(comment: EditReviewCommentModel, isNew: Boolean) {
        hideProgress()
        val intent = Intent()
        intent.putExtras(
            Bundler.start()
                .put(BundleConstant.ITEM, comment)
                .put(BundleConstant.EXTRA, isNew)
                .end()
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.submit) {
            if (PrefGetter.isSentViaEnabled) {
                val temp = editText.savedText.toString()
                if (!temp.contains(sentFromOctoHub)) {
                    editText.savedText = editText.savedText.toString() + sentFromOctoHub
                }
            }
            presenter.onHandleSubmission(
                editText.savedText,
                extraType,
                itemId,
                commentId,
                login,
                issueNumber,
                sha,
                reviewComment
            )
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        if (menu.findItem(R.id.submit) != null) {
            menu.findItem(R.id.submit).isEnabled = true
        }
        if (BundleConstant.ExtraType.FOR_RESULT_EXTRA.equals(extraType, ignoreCase = true)) {
            menu.findItem(R.id.submit).setIcon(R.drawable.ic_done)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun showProgress(@StringRes resId: Int) {
        super.showProgress(resId)
        invalidateOptionsMenu()
    }

    override fun hideProgress() {
        invalidateOptionsMenu()
        super.hideProgress()
    }

    override fun onBackPressed() {
        if (!InputHelper.isEmpty(editText)) {
            ViewHelper.hideKeyboard(editText)
            MessageDialogView.newInstance(
                getString(R.string.close), getString(R.string.unsaved_data_warning),
                Bundler.start()
                    .put("primary_extra", getString(R.string.discard))
                    .put("secondary_extra", getString(R.string.cancel))
                    .put(BundleConstant.EXTRA, true)
                    .end()
            )
                .show(supportFragmentManager, MessageDialogView.TAG)
            return

        }
        super.onBackPressed()
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk && bundle != null) {
            finish()
        }
    }

    override fun onAppendLink(title: String?, link: String?, isLink: Boolean) {
        markDownLayout.onAppendLink(title, link, isLink)
    }

    override fun getEditText(): EditText = editText

    override fun getSavedText(): CharSequence? = editText.savedText

    override fun fragmentManager(): FragmentManager = supportFragmentManager

    @SuppressLint("SetTextI18n")
    override fun onEmojiAdded(emoji: Emoji?) {
        markDownLayout.onEmojiAdded(emoji)
    }

    private fun onCreate() {
        val intent = intent
        if (intent != null && intent.extras != null) {
            val bundle = intent.extras!!
            extraType = bundle.getString(BundleConstant.EXTRA_TYPE)
            reviewComment = bundle.getParcelable(BundleConstant.REVIEW_EXTRA)
            itemId = bundle.getString(BundleConstant.ID)
            login = bundle.getString(BundleConstant.EXTRA_TWO)
            if (extraType.equals(
                    BundleConstant.ExtraType.EDIT_COMMIT_COMMENT_EXTRA,
                    ignoreCase = true
                )
                || extraType.equals(
                    BundleConstant.ExtraType.NEW_COMMIT_COMMENT_EXTRA,
                    ignoreCase = true
                )
            ) {
                sha = bundle.getString(BundleConstant.EXTRA_THREE)
            } else {
                issueNumber = bundle.getInt(BundleConstant.EXTRA_THREE)
            }
            commentId = bundle.getLong(BundleConstant.EXTRA_FOUR)
            val textToUpdate = bundle.getString(BundleConstant.EXTRA)
            if (!InputHelper.isEmpty(textToUpdate)) {
                editText.setText(String.format("%s ", textToUpdate))
                editText.setSelection(InputHelper.toString(editText).length)
            }
            if (bundle.getString("message", "").isNullOrEmpty()) {
                replyQuote.visibility = GONE
            } else {
                MarkDownProvider.setMdText(quote, bundle.getString("message", ""))
            }
            participants = bundle.getStringArrayList("participants")
        }
    }
}
