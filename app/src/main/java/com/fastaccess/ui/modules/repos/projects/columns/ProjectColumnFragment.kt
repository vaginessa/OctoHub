package com.fastaccess.ui.modules.repos.projects.columns

import android.content.Context
import android.os.Bundle
import android.view.View
import androidx.annotation.StringRes
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.dao.ProjectCardModel
import com.fastaccess.data.dao.ProjectColumnModel
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.Logger
import com.fastaccess.helper.PrefGetter
import com.fastaccess.provider.rest.loadmore.OnLoadMore
import com.fastaccess.ui.adapter.ColumnCardAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.repos.projects.crud.ProjectCurdDialogFragment
import com.fastaccess.ui.modules.repos.projects.details.ProjectPagerMvp
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.dialog.MessageDialogView
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Hashemsergani on 11.09.17.
 */
class ProjectColumnFragment : BaseFragment<ProjectColumnMvp.View, ProjectColumnPresenter>(),
    ProjectColumnMvp.View {

    val recycler: DynamicRecyclerView by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout by viewFind(R.id.refresh)
    val stateLayout: StateLayout by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller by viewFind(R.id.fastScroller)
    val columnName: FontTextView by viewFind(R.id.columnName)

    //    val editColumnHolder: View by viewFind(R.id.editColumnHolder)
    val editColumn: View by viewFind(R.id.editColumn)
    val addCard: View by viewFind(R.id.addCard)
    private val deleteColumn: View by viewFind(R.id.deleteColumn)

    private var onLoadMore: OnLoadMore<Long>? = null
    private val adapter by lazy { ColumnCardAdapter(presenter.getCards(), isOwner()) }
    private var pageCallback: ProjectPagerMvp.DeletePageListener? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        pageCallback = when {
            parentFragment is ProjectPagerMvp.DeletePageListener -> parentFragment as ProjectPagerMvp.DeletePageListener
            context is ProjectPagerMvp.DeletePageListener -> context
            else -> null
        }
    }

    override fun onDetach() {
        pageCallback = null
        super.onDetach()
    }

    fun onEditColumn() {
        ProjectCurdDialogFragment.newInstance(getColumn().name)
            .show(childFragmentManager, ProjectCurdDialogFragment.TAG)
    }

    fun onDeleteColumn() {
        MessageDialogView.newInstance(
            getString(R.string.delete), getString(R.string.confirm_message),
            false, MessageDialogView.getYesNoBundle(requireContext())
        )
            .show(childFragmentManager, MessageDialogView.TAG)
    }

    fun onAddCard() {
        ProjectCurdDialogFragment.newInstance(isCard = true)
            .show(childFragmentManager, ProjectCurdDialogFragment.TAG)
    }

    override fun onNotifyAdapter(items: List<ProjectCardModel>?, page: Int) {
        hideProgress()
        if (items == null || items.isEmpty()) {
            adapter.clear()
            return
        }
        if (page <= 1) {
            adapter.insertItems(items)
        } else {
            adapter.addItems(items)
        }
    }

    override fun getLoadMore(): OnLoadMore<Long> {
        if (onLoadMore == null) {
            onLoadMore = OnLoadMore(presenter)
        }
        onLoadMore!!.parameter = getColumn().id
        return onLoadMore!!
    }

    override fun providePresenter(): ProjectColumnPresenter = ProjectColumnPresenter()

    override fun fragmentLayout(): Int = R.layout.project_columns_layout

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        editColumn.setOnClickListener {
            onEditColumn()
        }
        deleteColumn.setOnClickListener {
            onDeleteColumn()
        }
        addCard.setOnClickListener {
            onAddCard()
        }
        val column = getColumn()
        columnName.text = column.name
        refresh.setOnRefreshListener { presenter.onCallApi(1, column.id) }
        stateLayout.setOnReloadListener { presenter.onCallApi(1, column.id) }
        stateLayout.setEmptyText(R.string.no_cards)
        recycler.setEmptyView(stateLayout, refresh)
        getLoadMore().initialize(presenter.currentPage, presenter.previousTotal)
        adapter.listener = presenter
        recycler.adapter = adapter
        recycler.addOnScrollListener(getLoadMore())
        fastScroller.attachRecyclerView(recycler)
        if (presenter.getCards().isEmpty() && !presenter.isApiCalled) {
            presenter.onCallApi(1, column.id)
        }
        addCard.visibility = if (isOwner()) View.VISIBLE else View.GONE
        deleteColumn.visibility = if (isOwner()) View.VISIBLE else View.GONE
        editColumn.visibility = if (isOwner()) View.VISIBLE else View.GONE
    }

    override fun showProgress(@StringRes resId: Int) {
        refresh.isRefreshing = true
        stateLayout.showProgress()
    }

    override fun hideProgress() {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
    }

    override fun showErrorMessage(msgRes: String) {
        showReload()
        super.showErrorMessage(msgRes)
    }

    override fun showMessage(titleRes: Int, msgRes: Int) {
        showReload()
        super.showMessage(titleRes, msgRes)
    }

    override fun onScrollTop(index: Int) {
        super.onScrollTop(index)
        recycler.scrollToPosition(0)
    }

    override fun onDestroyView() {
        recycler.removeOnScrollListener(getLoadMore())
        super.onDestroyView()
    }

    override fun onCreatedOrEdited(text: String, isCard: Boolean, position: Int) {
        Logger.e(text, isCard, position)
        if (!isCard) {
            columnName.text = text
            presenter.onEditOrDeleteColumn(text, getColumn())
        } else {
            if (position == -1) {
                presenter.createCard(text, getColumn().id!!)
            } else {
                presenter.editCard(text, adapter.getItem(position)!!, position)
            }
        }
    }

    override fun onMessageDialogActionClicked(isOk: Boolean, bundle: Bundle?) {
        super.onMessageDialogActionClicked(isOk, bundle)
        if (isOk) {
            if (bundle != null) {
                if (bundle.containsKey(BundleConstant.ID)) {
                    val position = bundle.getInt(BundleConstant.ID)
                    presenter.onDeleteCard(position, adapter.getItem(position)!!)
                } else {
                    presenter.onEditOrDeleteColumn(null, getColumn())
                }
            } else {
                presenter.onEditOrDeleteColumn(null, getColumn())
            }
        }
    }

    override fun deleteColumn() {
        pageCallback?.onDeletePage(getColumn())
        hideBlockingProgress()
    }

    override fun showBlockingProgress() {
        super.showProgress(0)
    }

    override fun hideBlockingProgress() {
        super.hideProgress()
    }

    override fun isOwner(): Boolean = requireArguments().getBoolean(BundleConstant.EXTRA)

    override fun onDeleteCard(position: Int) {
        val yesNoBundle = MessageDialogView.getYesNoBundle(requireContext())
        yesNoBundle.putInt(BundleConstant.ID, position)
        MessageDialogView.newInstance(
            getString(R.string.delete), getString(R.string.confirm_message),
            false, yesNoBundle
        ).show(childFragmentManager, MessageDialogView.TAG)
    }

    override fun onEditCard(note: String?, position: Int) {
        ProjectCurdDialogFragment.newInstance(note, true, position)
            .show(childFragmentManager, ProjectCurdDialogFragment.TAG)
    }

    override fun addCard(it: ProjectCardModel) {
        hideBlockingProgress()
        adapter.addItem(it, 0)
    }

    override fun updateCard(response: ProjectCardModel, position: Int) {
        hideBlockingProgress()
        adapter.swapItem(response, position)
    }

    override fun onRemoveCard(position: Int) {
        hideBlockingProgress()
        adapter.removeItem(position)
    }

    private fun showReload() {
        hideProgress()
        stateLayout.showReload(adapter.itemCount)
    }

    private fun getColumn(): ProjectColumnModel =
        requireArguments().getParcelable(BundleConstant.ITEM)!!

    companion object {
        fun newInstance(
            column: ProjectColumnModel,
            isCollaborator: Boolean
        ): ProjectColumnFragment {
            val fragment = ProjectColumnFragment()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ITEM, column)
                .put(BundleConstant.EXTRA, isCollaborator)
                .end()
            return fragment
        }
    }
}