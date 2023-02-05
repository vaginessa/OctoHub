package com.fastaccess.ui.modules.notification.octohub

import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.base.mvp.BaseMvp

/**
 * Created by Kosh on 19.11.17.
 */
interface OctoHubNotificationsMvp {

    interface View : BaseMvp.FAView, BaseViewHolder.OnItemClickListener<OctoHubNotification> {
        fun notifyAdapter(items: List<OctoHubNotification>?)
    }

    interface Presenter {
        fun getData(): List<OctoHubNotification>
        fun load()
    }
}
