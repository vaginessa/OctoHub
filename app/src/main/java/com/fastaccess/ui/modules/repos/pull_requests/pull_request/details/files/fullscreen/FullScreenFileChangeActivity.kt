package com.fastaccess.ui.modules.repos.pull_requests.pull_request.details.files.fullscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.CommentRequestModel
import com.fastaccess.data.dao.CommitFileChanges
import com.fastaccess.data.dao.CommitLinesModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.CommitLinesAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.modules.reviews.AddReviewDialogFragment
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Hashemsergani on 24.09.17.
 */

class FullScreenFileChangeActivity :
    BaseActivity<FullScreenFileChangeMvp.View, FullScreenFileChangePresenter>(),
    FullScreenFileChangeMvp.View {
    val recycler: DynamicRecyclerView by lazy { viewFind(R.id.recycler)!! }
    val refresh: SwipeRefreshLayout by lazy { viewFind(R.id.refresh)!! }
    val stateLayout: StateLayout by lazy { viewFind(R.id.stateLayout)!! }
    val fastScroller: RecyclerViewFastScroller by lazy { viewFind(R.id.fastScroller)!! }
    val changes: TextView by lazy { viewFind(R.id.changes)!! }
    val deletion: TextView by lazy { viewFind(R.id.deletion)!! }
    val addition: TextView by lazy { viewFind(R.id.addition)!! }

    val commentList = arrayListOf<CommentRequestModel>()

    private val adapter by lazy { CommitLinesAdapter(arrayListOf(), this) }

    override fun layout(): Int = R.layout.full_screen_file_changes_layout

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): FullScreenFileChangePresenter = FullScreenFileChangePresenter()

    override fun onNotifyAdapter(model: CommitLinesModel) {
        adapter.addItem(model)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showMessage(titleRes: String, msgRes: String) {
        hideProgress()
        super.showMessage(titleRes, msgRes)
    }

    override fun showErrorMessage(msgRes: String) {
        hideProgress()
        super.showErrorMessage(msgRes)
    }

    override fun showProgress(resId: Int) {
        stateLayout.showProgress()
        refresh.isRefreshing = true
    }

    override fun hideProgress() {
        stateLayout.hideProgress()
        refresh.isRefreshing = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        refresh.isEnabled = false
        recycler.adapter = adapter
        val padding = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        recycler.setPadding(padding, 0, padding, 0)
        fastScroller.attachRecyclerView(recycler)
        presenter.onLoad(intent)
        presenter.model?.let { model ->
            title = Uri.parse(model.commitFileModel?.filename).lastPathSegment
            addition.text = model.commitFileModel?.additions.toString()
            deletion.text = model.commitFileModel?.deletions.toString()
            changes.text = model.commitFileModel?.changes.toString()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.done_menu, menu)
        menu.findItem(R.id.submit)?.setIcon(R.drawable.ic_done)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.submit -> {
                sendResult()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onItemClick(position: Int, v: View?, item: CommitLinesModel) {
        if (item.text?.startsWith("@@")!!) return
        val commit = presenter.model?.commitFileModel ?: return
        AddReviewDialogFragment.newInstance(
            item, Bundler.start()
                .put(BundleConstant.ITEM, commit.filename)
                .put(BundleConstant.EXTRA_TWO, presenter.position)
                .put(BundleConstant.EXTRA_THREE, position)
                .end()
        )
            .show(supportFragmentManager, "AddReviewDialogFragment")
    }

    override fun onItemLongClick(position: Int, v: View?, item: CommitLinesModel) {

    }

    override fun onCommentAdded(comment: String, item: CommitLinesModel, bundle: Bundle?) {
        if (bundle != null) {
            val path = bundle.getString(BundleConstant.ITEM) ?: return
            val commentRequestModel = CommentRequestModel()
            commentRequestModel.body = comment
            commentRequestModel.path = path
            commentRequestModel.position = item.position
            commentList.add(commentRequestModel)
            val childPosition = bundle.getInt(BundleConstant.EXTRA_THREE)
            val current = adapter.getItem(childPosition)
            if (current != null) {
                current.isHasCommentedOn = true
                adapter.swapItem(current, childPosition)
            }
            if (presenter.isCommit) {
                sendResult()
            }
        }
    }

    private fun sendResult() {
        val intent = Intent()
        intent.putExtras(
            Bundler.start().putParcelableArrayList(BundleConstant.ITEM, commentList).end()
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }


    companion object {
        fun startLauncherForResult(
            context: Context,
            launcher: ActivityResultLauncher<Intent>,
            model: CommitFileChanges,
            position: Int,
            isCommit: Boolean = false,
        ) {
            val intent = Intent(context, FullScreenFileChangeActivity::class.java)
            intent.putExtras(
                Bundler.start()
                    .put(BundleConstant.EXTRA, model)
                    .put(BundleConstant.ITEM, position)
                    .put(BundleConstant.YES_NO_EXTRA, isCommit)
                    .end()
            )
            launcher.launch(intent)
        }
    }
}