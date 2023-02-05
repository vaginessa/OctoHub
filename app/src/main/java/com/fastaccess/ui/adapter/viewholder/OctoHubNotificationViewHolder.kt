package com.fastaccess.ui.adapter.viewholder

import android.view.View
import com.fastaccess.R
import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.helper.ParseDateFormat
import com.fastaccess.ui.adapter.OctoHubNotificationsAdapter
import com.fastaccess.ui.base.adapter.BaseViewHolder
import com.fastaccess.ui.widgets.FontTextView

/**
 * Created: by Kosh on 02 Jun 2017, 1:27 PM
 */

open class OctoHubNotificationViewHolder(
    itemView: View,
    adapter: OctoHubNotificationsAdapter
) : BaseViewHolder<OctoHubNotification>(itemView, adapter) {
    val title: FontTextView = itemView.findViewById(R.id.title)
    val date: FontTextView = itemView.findViewById(R.id.date)
    val type: FontTextView = itemView.findViewById(R.id.type)


    override fun bind(t: OctoHubNotification) {
        title.text = t.title
        if (t.date != null) {
            date.text = ParseDateFormat.getTimeAgo(t.date)
        }
        type.text = t.type!!.name.replace("_", " ")
    }

}