package com.fastaccess.data.dao.types

import androidx.annotation.DrawableRes
import com.fastaccess.R
import com.google.gson.annotations.SerializedName

enum class IssueEventType(@get:DrawableRes @param:DrawableRes val iconResId: Int = R.drawable.ic_label) {

    @SerializedName("assigned")
    assigned(R.drawable.ic_profile),

    @SerializedName("closed")
    closed(R.drawable.ic_issue_closed),

    @SerializedName("commented")
    commented(R.drawable.ic_comment),

    @SerializedName("committed")
    committed(R.drawable.ic_push),

    @SerializedName("demilestoned")
    demilestoned(R.drawable.ic_milestone),

    @SerializedName("head_ref_deleted")
    head_ref_deleted(R.drawable.ic_trash),

    @SerializedName("head_ref_restored")
    head_ref_restored(R.drawable.ic_redo),

    @SerializedName("labeled")
    labeled(R.drawable.ic_label),

    @SerializedName("locked")
    locked(R.drawable.ic_lock),

    @SerializedName("mentioned")
    mentioned(R.drawable.ic_at),

    @SerializedName("merged")
    merged(R.drawable.ic_fork),

    @SerializedName("milestoned")
    milestoned(R.drawable.ic_milestone),

    @SerializedName("referenced")
    referenced(R.drawable.ic_format_quote),

    @SerializedName("renamed")
    renamed(R.drawable.ic_edit),

    @SerializedName("reopened")
    reopened(R.drawable.ic_issue_opened),

    @SerializedName("subscribed")
    subscribed(R.drawable.ic_subscribe),

    @SerializedName("unassigned")
    unassigned(R.drawable.ic_profile),

    @SerializedName("unlabeled")
    unlabeled(R.drawable.ic_label),

    @SerializedName("unlocked")
    unlocked(R.drawable.ic_unlock),

    @SerializedName("unsubscribed")
    unsubscribed(R.drawable.ic_eye_off),

    @SerializedName("review_requested")
    review_requested(R.drawable.ic_eye),

    @SerializedName("review_dismissed")
    review_dismissed(R.drawable.ic_eye_off),

    @SerializedName("review_request_removed")
    review_request_removed(R.drawable.ic_eye_off),

    @SerializedName("cross-referenced")
    cross_referenced(R.drawable.ic_format_quote),

    @SerializedName("reviewed")
    reviewed(R.drawable.ic_eye),

    @SerializedName("added_to_project")
    added_to_project(R.drawable.ic_add),

    @SerializedName("deployed")
    deployed(R.drawable.ic_rocket),

    line_commented(R.drawable.ic_comment),
    commit_commented(R.drawable.ic_comment),
    changes_requested(R.drawable.ic_eye),
    GROUPED(R.drawable.ic_eye),
    unknown(R.drawable.ic_bug);


    companion object {
        fun getType(type: String) = values()
            .asSequence()
            .filter {
                it.name == type.replace("-".toRegex(), "_")
            }
            .firstOrNull() ?: unknown
    }
}