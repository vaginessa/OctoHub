package com.fastaccess.data.entity.converters

import com.fastaccess.data.entity.OctoHubNotification
import io.objectbox.converter.PropertyConverter

class NotificationTypeConverter : PropertyConverter<OctoHubNotification.NotificationType?, Int?> {
    override fun convertToEntityProperty(databaseValue: Int?): OctoHubNotification.NotificationType? {
        if (databaseValue == null || databaseValue == -1) {
            return null
        }
        return OctoHubNotification.NotificationType.values()[databaseValue]
    }

    override fun convertToDatabaseValue(entityProperty: OctoHubNotification.NotificationType?): Int {
        return entityProperty?.ordinal ?: -1
    }
}