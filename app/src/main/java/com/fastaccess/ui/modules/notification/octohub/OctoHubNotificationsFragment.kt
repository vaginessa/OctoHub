package com.fastaccess.ui.modules.notification.octohub

import android.os.Bundle
import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.fastaccess.R
import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.ui.adapter.OctoHubNotificationsAdapter
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.delegate.viewFind
import com.fastaccess.ui.modules.main.notifications.OctoHubNotificationDialog
import com.fastaccess.ui.widgets.StateLayout
import com.fastaccess.ui.widgets.recyclerview.DynamicRecyclerView
import com.fastaccess.ui.widgets.recyclerview.scroll.RecyclerViewFastScroller

/**
 * Created by Kosh on 19.11.17.
 */
class OctoHubNotificationsFragment :
    BaseFragment<OctoHubNotificationsMvp.View, OctoHubNotificationsPresenter>(),
    OctoHubNotificationsMvp.View {
    val recycler: DynamicRecyclerView by viewFind(R.id.recycler)
    val refresh: SwipeRefreshLayout by viewFind(R.id.refresh)
    val stateLayout: StateLayout by viewFind(R.id.stateLayout)
    val fastScroller: RecyclerViewFastScroller by viewFind(R.id.fastScroller)
    private val adapter by lazy { OctoHubNotificationsAdapter(presenter.getData().toMutableList()) }

    override fun fragmentLayout(): Int = R.layout.small_grid_refresh_list

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        adapter.listener = this
        stateLayout.setEmptyText(R.string.no_notifications)
        recycler.setEmptyView(stateLayout, refresh)
        recycler.addDivider()
        refresh.setOnRefreshListener { presenter.load() }
        stateLayout.setOnReloadListener { presenter.load() }
        if (savedInstanceState == null) {
            stateLayout.showProgress()
            presenter.load()
        }
        recycler.adapter = adapter
        fastScroller.attachRecyclerView(recycler)
    }

    override fun providePresenter(): OctoHubNotificationsPresenter = OctoHubNotificationsPresenter()

    override fun notifyAdapter(items: List<OctoHubNotification>?) {
        refresh.isRefreshing = false
        stateLayout.hideProgress()
        if (items != null) {
            adapter.insertItems(items)
        } else {
            adapter.clear()
        }
    }

    override fun onItemClick(position: Int, v: View?, item: OctoHubNotification) {
        presenter.manageDisposable(
            OctoHubNotificationDialog.show(childFragmentManager, item)
        )
    }


    override fun onItemLongClick(position: Int, v: View?, item: OctoHubNotification) {}
}