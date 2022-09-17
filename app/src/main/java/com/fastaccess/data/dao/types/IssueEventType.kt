package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R
import com.google.gson.annotations.SerializedName

/**
 * @see <a href="https://docs.github.com/es/developers/webhooks-and-events/events/issue-event-types">Issue Event Names</a>
 */
enum class IssueEventType(@get:DrawableRes @param:DrawableRes val iconResId: Int = R.drawable.ic_label) {

    @SerializedName("assigned")
    ASSIGNED(R.drawable.ic_profile),

    @SerializedName("closed")
    CLOSED(R.drawable.ic_issue_closed),

    @SerializedName("commented")
    COMMENTED(R.drawable.ic_comment),

    @SerializedName("committed")
    COMMITTED(R.drawable.ic_push),

    @SerializedName("demilestoned")
    DEMILESTONED(R.drawable.ic_milestone),

    @SerializedName("head_ref_deleted")
    HEAD_REF_DELETED(R.drawable.ic_trash),

    @SerializedName("head_ref_restored")
    HEAD_REF_RESTORED(R.drawable.ic_redo),

    @SerializedName("labeled")
    LABELED(R.drawable.ic_label),

    @SerializedName("locked")
    LOCKED(R.drawable.ic_lock),

    @SerializedName("mentioned")
    MENTIONED(R.drawable.ic_at),

    @SerializedName("merged")
    MERGED(R.drawable.ic_fork),

    @SerializedName("milestoned")
    MILESTONED(R.drawable.ic_milestone),

    @SerializedName("referenced")
    REFERENCED(R.drawable.ic_format_quote),

    @SerializedName("renamed")
    RENAMED(R.drawable.ic_edit),

    @SerializedName("reopened")
    REOPENED(R.drawable.ic_issue_opened),

    @SerializedName("subscribed")
    SUBSCRIBED(R.drawable.ic_subscribe),

    @SerializedName("unassigned")
    UNASSIGNED(R.drawable.ic_profile),

    @SerializedName("unlabeled")
    UNLABELED(R.drawable.ic_label),

    @SerializedName("unlocked")
    UNLOCKED(R.drawable.ic_unlock),

    @SerializedName("unsubscribed")
    UNSUBSCRIBED(R.drawable.ic_eye_off),

    @SerializedName("review_requested")
    REVIEW_REQUESTED(R.drawable.ic_eye),

    @SerializedName("review_dismissed")
    REVIEW_DISMISSED(R.drawable.ic_eye_off),

    @SerializedName("review_request_removed")
    REVIEW_REQUEST_REMOVED(R.drawable.ic_eye_off),

    @SerializedName("cross-referenced")
    CROSS_REFERENCED(R.drawable.ic_format_quote),

    @SerializedName("reviewed")
    REVIEWED(R.drawable.ic_eye),

    @SerializedName("added_to_project")
    ADDED_TO_PROJECT(R.drawable.ic_add),

    @SerializedName("deployed")
    DEPLOYED(R.drawable.ic_rocket),

    LINE_COMMENTED(R.drawable.ic_comment),
    COMMIT_COMMENTED(R.drawable.ic_comment),
    CHANGES_REQUESTED(R.drawable.ic_eye),
    GROUPED(R.drawable.ic_eye),
    UNKNOWN(R.drawable.ic_bug);


    companion object {
        fun getType(type: String) = values()
            .asSequence()
            .filter { it.name == type.replace("-".toRegex(), "_").uppercase() }
            .firstOrNull() ?: UNKNOWN
    }
}