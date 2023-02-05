package com.fastaccess.ui.modules.notification.octohub

import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.data.entity.dao.OctoHubNotificationDao
import com.fastaccess.ui.base.mvp.presenter.BasePresenter

/**
 * Created by Kosh on 19.11.17.
 */
class OctoHubNotificationsPresenter : BasePresenter<OctoHubNotificationsMvp.View>(),
    OctoHubNotificationsMvp.Presenter {
    private val data = mutableListOf<OctoHubNotification>()

    override fun getData(): List<OctoHubNotification> = data

    override fun load() {
        manageObservable(
            OctoHubNotificationDao.getNotifications()
                .toList()
                .toObservable()
                .doOnNext {
                    sendToView { v -> v.notifyAdapter(it) }
                })
    }
}