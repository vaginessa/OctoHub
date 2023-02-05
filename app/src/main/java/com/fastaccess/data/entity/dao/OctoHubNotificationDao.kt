package com.fastaccess.data.entity.dao

import com.fastaccess.data.db.ObjectBox
import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.data.entity.OctoHubNotification_
import com.fastaccess.utils.Optional
import com.fastaccess.utils.toObservable
import com.fastaccess.utils.toSingle
import com.fastaccess.utils.toSingleOptional
import io.objectbox.Box
import io.objectbox.kotlin.boxFor
import io.reactivex.Observable
import io.reactivex.Single

class OctoHubNotificationDao {
    companion object {
        val box: Box<OctoHubNotification> by lazy { ObjectBox.boxStore.boxFor() }

        fun update(notification: OctoHubNotification): Single<Long> {
            return box.toSingle {
                it.put(notification)
            }
        }

        fun save(notification: OctoHubNotification): Single<Long> {
            return update(notification)
        }

        fun getLatest(): Single<Optional<OctoHubNotification>> {
            return box.query()
                .equal(OctoHubNotification_.read, false)
                .orderDesc(OctoHubNotification_.date)
                .build()
                .toSingleOptional {
                    it.findFirst()
                }
        }

        fun getNotifications(): Observable<OctoHubNotification> {
            return box.query()
                .orderDesc(OctoHubNotification_.date)
                .build()
                .toObservable {
                    it.find()
                }.flatMap {
                    Observable.fromIterable(it)
                }
        }

        fun hasNotifications(): Single<Boolean> {
            return box.toSingle { it.count() > 0 }
        }
    }
}