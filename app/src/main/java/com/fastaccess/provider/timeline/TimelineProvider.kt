package com.fastaccess.provider.timeline

import android.content.Context
import android.graphics.Color
import android.text.style.BackgroundColorSpan
import com.fastaccess.R
import com.fastaccess.data.dao.timeline.GenericEvent
import com.fastaccess.data.dao.types.IssueEventType
import com.fastaccess.data.entity.User
import com.fastaccess.helper.InputHelper.isEmpty
import com.fastaccess.helper.ParseDateFormat.Companion.getTimeAgo
import com.fastaccess.helper.PrefGetter.themeType
import com.fastaccess.helper.ViewHelper
import com.fastaccess.provider.timeline.HtmlHelper.getWindowBackground
import com.fastaccess.ui.widgets.SpannableBuilder
import com.fastaccess.ui.widgets.SpannableBuilder.Companion.builder
import com.zzhoujay.markdown.style.CodeSpan
import java.util.*

/**
 * Created by Kosh on 20 Apr 2017, 7:18 PM
 */
object TimelineProvider {
    @JvmStatic
    fun getStyledEvents(
        issueEventModel: GenericEvent,
        context: Context, isMerged: Boolean
    ): SpannableBuilder {

        val event = issueEventModel.event
        val spannableBuilder = builder()

        val date = when {
            issueEventModel.createdAt != null -> issueEventModel.createdAt
            issueEventModel.author != null -> issueEventModel.author!!.date
            else -> null
        }

        if (event != null) {

            val eventName = event.name.replaceAndLowercase()

            val to = context.getString(R.string.to)
            val from = context.getString(R.string.from)
            val thisString = context.getString(R.string.this_value)
            val `in` = context.getString(R.string.in_value)

            when {
                event === IssueEventType.LABELED || event === IssueEventType.UNLABELED -> {

                    spannableBuilder
                        .bold(
                            when {
                                issueEventModel.actor != null -> issueEventModel.actor!!.login!!
                                else -> "anonymous"
                            }
                        ).append(" $eventName ")

                    val labelModel = issueEventModel.label!!
                    val color = Color.parseColor("#" + labelModel.color)

                    spannableBuilder
                        .append(
                            text = " ${labelModel.name?.lowercase()} ",
                            span = CodeSpan(color, ViewHelper.generateTextColor(color), 5F)
                        )
                        .append(" ${getDate(issueEventModel.createdAt)}")

                }
                event === IssueEventType.COMMITTED -> {
                    spannableBuilder
                        .append("${issueEventModel.message!!.replace("\n".toRegex(), " ")} ")
                        .url(substring(issueEventModel.sha))
                }
                else -> {

                    val user = when {
                        issueEventModel.assignee != null && issueEventModel.assigner != null -> {
                            issueEventModel.assigner
                        }
                        issueEventModel.actor != null -> issueEventModel.actor
                        issueEventModel.author != null -> issueEventModel.author
                        else -> null
                    }

                    if (user != null) spannableBuilder.bold(user.login!!)

                    when {
                        user != null && (event === IssueEventType.REVIEW_REQUESTED ||
                                event === IssueEventType.REVIEW_DISMISSED ||
                                event === IssueEventType.REVIEW_REQUEST_REMOVED) -> {

                            appendReviews(
                                issueEventModel = issueEventModel,
                                event = event,
                                spannableBuilder = spannableBuilder,
                                from = from,
                                user = issueEventModel.reviewRequester!!
                            )
                        }
                        event === IssueEventType.CLOSED || event === IssueEventType.REOPENED -> {

                            when {
                                isMerged -> spannableBuilder.append(" ${IssueEventType.MERGED.name}")
                                else -> spannableBuilder
                                    .append(" $eventName ")
                                    .append(thisString)
                            }

                            if (issueEventModel.commitId != null) {
                                spannableBuilder
                                    .append(" $`in` ")
                                    .url(substring(issueEventModel.commitId))
                            }

                        }
                        event === IssueEventType.ASSIGNED || event === IssueEventType.UNASSIGNED -> {

                            spannableBuilder.append(" ")

                            when {
                                user != null && issueEventModel.assignee != null &&
                                        user.login == issueEventModel.assignee!!.login -> {

                                    spannableBuilder.append(
                                        when {
                                            event === IssueEventType.ASSIGNED -> "self-assigned this"
                                            else -> "removed their assignment"
                                        }
                                    )
                                }
                                else -> {
                                    spannableBuilder.append(
                                        when {
                                            event === IssueEventType.ASSIGNED -> "assigned"
                                            else -> "unassigned"
                                        }
                                    )
                                    spannableBuilder
                                        .append(" ")
                                        .bold(
                                            when {
                                                issueEventModel.assignee != null -> issueEventModel.assignee!!.login!!
                                                else -> ""
                                            }
                                        )
                                }
                            }

                        }
                        event === IssueEventType.LOCKED || event === IssueEventType.UNLOCKED -> {
                            spannableBuilder
                                .append(" ")
                                .append(
                                    when {
                                        event === IssueEventType.LOCKED -> "locked and limited conversation to collaborators"
                                        else -> "unlocked this conversation"
                                    }
                                )
                        }
                        event === IssueEventType.HEAD_REF_DELETED || event === IssueEventType.HEAD_REF_RESTORED -> {
                            spannableBuilder
                                .append(" ")
                                .append(
                                    " $eventName ",
                                    BackgroundColorSpan(getWindowBackground(themeType))
                                )
                        }
                        event === IssueEventType.MILESTONED || event === IssueEventType.DEMILESTONED -> {
                            spannableBuilder
                                .append(" ")
                                .append(
                                    when {
                                        event === IssueEventType.MILESTONED -> "added this to the"
                                        else -> "removed this from the"
                                    }
                                )
                                .bold(" ${issueEventModel.milestone!!.title!!} ")
                                .append("milestone")
                        }
                        event === IssueEventType.DEPLOYED -> spannableBuilder.bold(" deployed")
                        else -> spannableBuilder.append(" $eventName")
                    }


                    when {
                        event === IssueEventType.RENAMED -> {

                            spannableBuilder
                                .append(" $from ")
                                .bold(issueEventModel.rename!!.fromValue!!)
                                .append(" $to ")
                                .bold(issueEventModel.rename!!.toValue!!)

                        }
                        event === IssueEventType.REFERENCED || event === IssueEventType.MERGED -> {
                            spannableBuilder
                                .append(" commit ")
                                .url(substring(issueEventModel.commitId))
                        }
                        event === IssueEventType.CROSS_REFERENCED -> {

                            val sourceModel = issueEventModel.source
                            if (sourceModel != null) {

                                var type = sourceModel.type
                                val title = builder()
                                when {
                                    sourceModel.pullRequest != null -> {
                                        if (sourceModel.issue != null) title.url("#" + sourceModel.issue!!.number)
                                        type = "pull request"
                                    }
                                    sourceModel.issue != null -> {
                                        title.url("#" + sourceModel.issue!!.number)
                                    }
                                    sourceModel.commit != null -> {
                                        title.url(substring(sourceModel.commit!!.sha))
                                    }
                                    sourceModel.repository != null -> {
                                        title.url(sourceModel.repository!!.name!!)
                                    }
                                }

                                if (!isEmpty(title)) {
                                    spannableBuilder
                                        .append(" $thisString in $type ")
                                        .append(title)
                                }
                            }
                        }
                    }
                    spannableBuilder.append(" ${getDate(date)}")
                }
            }
        }
        return spannableBuilder
    }

    private fun appendReviews(
        issueEventModel: GenericEvent, event: IssueEventType,
        spannableBuilder: SpannableBuilder, from: String,
        user: User
    ) {

        spannableBuilder.append(" ")
        val reviewer = issueEventModel.requestedReviewer

        when {
            reviewer != null && user.login == reviewer.login -> {
                spannableBuilder.append(
                    when {
                        event === IssueEventType.REVIEW_REQUESTED -> "self-requested a review"
                        else -> "removed their request for review"
                    }
                )
            }
            else -> {
                spannableBuilder
                    .append(
                        when {
                            event === IssueEventType.REVIEW_REQUESTED -> "Requested a review"
                            else -> "dismissed the review"
                        }
                    )
                    .append(" ")
                    .append(
                        when {
                            reviewer != null && reviewer.login == user.login -> from
                            else -> " "
                        }
                    )
                    .append(
                        when {
                            reviewer != null && reviewer.login == user.login -> " "
                            else -> ""
                        }
                    )
            }
        }

        when {
            issueEventModel.requestedTeam != null -> {

                val name = when {
                    !isEmpty(issueEventModel.requestedTeam!!.name) -> issueEventModel.requestedTeam!!.name
                    else -> issueEventModel.requestedTeam!!.slug
                }

                spannableBuilder
                    .bold(name!!)
                    .append(" team")

            }
            reviewer != null && user.login == reviewer.login -> {
                spannableBuilder.bold(issueEventModel.requestedReviewer!!.login!!)
            }
        }
    }

    private fun getDate(date: Date?) = getTimeAgo(date)

    private fun substring(value: String?) = when {
        value == null -> ""
        value.length <= 7 -> value
        else -> value.substring(0, 7)
    }

    private fun String.replaceAndLowercase() = replace("_".toRegex(), " ").lowercase()
}