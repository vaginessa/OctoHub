package com.fastaccess.ui.adapter

import android.view.ViewGroup
import com.fastaccess.R
import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.ui.adapter.viewholder.OctoHubNotificationViewHolder
import com.fastaccess.ui.base.adapter.BaseRecyclerAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder

/**
 * Created by Kosh on 02 Jun 2017, 1:36 PM
 */

class OctoHubNotificationsAdapter(data: MutableList<OctoHubNotification>) : BaseRecyclerAdapter<OctoHubNotification,
        OctoHubNotificationViewHolder, BaseViewHolder.OnItemClickListener<OctoHubNotification>>(data) {

    override fun viewHolder(parent: ViewGroup, viewType: Int): OctoHubNotificationViewHolder {
        return OctoHubNotificationViewHolder(BaseViewHolder.getView(parent, R.layout.octohub_notification_row_item), this)
    }

    override fun onBindView(holder: OctoHubNotificationViewHolder, position: Int) {
        holder.bind(getItem(position)!!)
    }

}