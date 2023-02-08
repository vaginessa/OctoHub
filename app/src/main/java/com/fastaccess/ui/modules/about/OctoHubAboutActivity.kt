package com.fastaccess.ui.modules.about

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.danielstone.materialaboutlibrary.ConvenienceBuilder
import com.danielstone.materialaboutlibrary.MaterialAboutActivity
import com.danielstone.materialaboutlibrary.items.MaterialAboutActionItem
import com.danielstone.materialaboutlibrary.model.MaterialAboutCard
import com.danielstone.materialaboutlibrary.model.MaterialAboutList
import com.fastaccess.App
import com.fastaccess.BuildConfig
import com.fastaccess.R
import com.fastaccess.helper.ActivityHelper
import com.fastaccess.provider.tasks.version.CheckVersionService
import com.fastaccess.provider.theme.ThemeEngine.applyForAbout
import com.fastaccess.ui.modules.changelog.ChangelogBottomSheetDialog
import com.fastaccess.ui.modules.repos.RepoPagerActivity
import com.fastaccess.ui.modules.repos.issues.create.CreateIssueActivity
import com.fastaccess.ui.modules.user.UserPagerActivity.Companion.startActivity
import com.mikepenz.aboutlibraries.LibsBuilder
import es.dmoral.toasty.Toasty

class OctoHubAboutActivity : MaterialAboutActivity() {
    private lateinit var malRecyclerview: View

    val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == Activity.RESULT_OK) {
            Toasty.success(
                App.getInstance(),
                getString(R.string.thank_you_for_feedback),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        applyForAbout(this)
        super.onCreate(savedInstanceState)
        malRecyclerview = findViewById(R.id.mal_recyclerview)
    }

    override fun getMaterialAboutList(context: Context): MaterialAboutList {
        val appCardBuilder = MaterialAboutCard.Builder()
        buildApp(context, appCardBuilder)
        val miscCardBuilder = MaterialAboutCard.Builder()
        buildMisc(context, miscCardBuilder)
        val revivalAppCardBuilder = MaterialAboutCard.Builder()
        buildRevivalApp(context, revivalAppCardBuilder)
        val libreAppCardBuilder = MaterialAboutCard.Builder()
        buildLibreApp(context, libreAppCardBuilder)
        val originalAppCardBuilder = MaterialAboutCard.Builder()
        buildOriginalApp(context, originalAppCardBuilder)
        val currentLogo = MaterialAboutCard.Builder()
        buildLogo(context, currentLogo)
        return MaterialAboutList(
            appCardBuilder.build(), miscCardBuilder.build(), revivalAppCardBuilder.build(),
            libreAppCardBuilder.build(), originalAppCardBuilder.build(), currentLogo.build()
        )
    }

    override fun getActivityTitle(): CharSequence {
        return getString(R.string.app_name)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            finish()
        }
        return false //override
    }

    private fun buildApp(context: Context, appCardBuilder: MaterialAboutCard.Builder) {
        appCardBuilder.title(R.string.about)
            .addItem(MaterialAboutActionItem.Builder()
                .text(getString(R.string.version))
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_update))
                .subText(BuildConfig.VERSION_NAME)
                .setOnClickAction { startService(Intent(this, CheckVersionService::class.java)) }
                .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.changelog)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_track_changes))
                .setOnClickAction {
                    ChangelogBottomSheetDialog().show(
                        supportFragmentManager,
                        "ChangelogBottomSheetDialog"
                    )
                }
                .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.app_name_full)
                .subText(R.string.app_description)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                .setOnClickAction {
                    startActivity(RepoPagerActivity.createIntent(this, "OctoHub", "HardcodedCat"))
                }
                .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.report_issue)
                .subText(R.string.report_issue_here)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_bug))
                .setOnClickAction {
                    CreateIssueActivity.startForResult(
                        launcher,
                        CreateIssueActivity.startForResult(this),
                        malRecyclerview
                    )
                }
                .build())
    }

    private fun buildMisc(context: Context, miscCardBuilder: MaterialAboutCard.Builder) {
        miscCardBuilder.title(R.string.misc)
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.open_source_libs)
                .subText(R.string.open_source_libs_desc)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_license))
                .setOnClickAction {
                    val builder = LibsBuilder()
                        .withSearchEnabled(true)
                        .withShowLoadingProgress(true)
                        .withActivityTitle(getString(R.string.open_source_libs))
                        .withAboutIconShown(true)
                        .withAboutVersionShown(true)
                        .withAboutAppName(getString(R.string.app_name))
                        .withAboutVersionString(BuildConfig.VERSION_NAME)
                        .withAboutDescription(getString(R.string.app_description))
                    val intent = Intent(this, CommonLibsActivity::class.java)
                    intent.putExtra("data", builder)
                    intent.putExtra(LibsBuilder.BUNDLE_TITLE, builder.activityTitle)
                    intent.putExtra(LibsBuilder.BUNDLE_EDGE_TO_EDGE, builder.edgeToEdge)
                    intent.putExtra(LibsBuilder.BUNDLE_SEARCH_ENABLED, builder.searchEnabled)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                }
                .build())
    }

    private fun buildRevivalApp(context: Context, revivalAppCardBuilder: MaterialAboutCard.Builder) {
        revivalAppCardBuilder.title(R.string.revival_app_name)
        revivalAppCardBuilder.addItem(MaterialAboutActionItem.Builder()
            .text(R.string.revival_app_author)
            .subText(R.string.revival_app_author_desc)
            .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
            .setOnClickAction {
                startActivity(
                    context, "LightDestory",
                    isOrg = false,
                    isEnterprise = false,
                    index = 0
                )
            }
            .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.revival_app_name)
                .subText(R.string.revival_app_desc)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                .setOnClickAction {
                    startActivity(
                        RepoPagerActivity.createIntent(
                            this,
                            "FastHub-RE",
                            "LightDestory"
                        )
                    )
                }
                .build())
            .addItem(
                ConvenienceBuilder.createEmailItem(
                    context,
                    ContextCompat.getDrawable(context, R.drawable.ic_email),
                    getString(R.string.send_email),
                    true,
                    getString(R.string.lightdestory_email),
                    getString(R.string.question_concerning_octohub)
                )
            )
    }

    private fun buildLibreApp(context: Context, libreAppCardBuilder: MaterialAboutCard.Builder) {
        libreAppCardBuilder.title(R.string.libre_app_name)
        libreAppCardBuilder.addItem(MaterialAboutActionItem.Builder()
            .text(R.string.libre_app_author)
            .subText(R.string.libre_app_author_desc)
            .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
            .setOnClickAction {
                startActivity(
                    context, "thermatk",
                    isOrg = false,
                    isEnterprise = false,
                    index = 0
                )
            }
            .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.libre_app_name)
                .subText(R.string.libre_app_desc)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                .setOnClickAction {
                    startActivity(
                        RepoPagerActivity.createIntent(
                            this,
                            "FastHub-Libre",
                            "thermatk"
                        )
                    )
                }
                .build())
            .addItem(
                ConvenienceBuilder.createEmailItem(
                    context,
                    ContextCompat.getDrawable(context, R.drawable.ic_email),
                    getString(R.string.send_email),
                    true,
                    getString(R.string.thermatk_email),
                    getString(R.string.question_concerning_octohub)
                )
            )
    }

    private fun buildOriginalApp(context: Context, originalAppCardBuilder: MaterialAboutCard.Builder) {
        originalAppCardBuilder.title(R.string.original_app_state)
        originalAppCardBuilder.addItem(MaterialAboutActionItem.Builder()
            .text(R.string.original_app_author)
            .subText(R.string.original_app_author_desc)
            .icon(ContextCompat.getDrawable(context, R.drawable.ic_profile))
            .setOnClickAction {
                startActivity(
                    context, "k0shk0sh",
                    isOrg = false,
                    isEnterprise = false,
                    index = 0
                )
            }
            .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.original_app_name)
                .subText(R.string.original_app_desc)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_github))
                .setOnClickAction {
                    startActivity(
                        RepoPagerActivity.createIntent(
                            this,
                            "FastHub",
                            "k0shk0sh"
                        )
                    )
                }
                .build())
            .addItem(
                ConvenienceBuilder.createEmailItem(
                    context,
                    ContextCompat.getDrawable(context, R.drawable.ic_email),
                    getString(R.string.send_email),
                    true,
                    getString(R.string.email_address),
                    getString(R.string.question_concerning_octohub)
                )
            )
    }

    private fun buildLogo(context: Context, currentLogo: MaterialAboutCard.Builder) {
        currentLogo.title(getString(R.string.current_logo_designer, "Benjamin J sperry"))
        currentLogo.addItem(MaterialAboutActionItem.Builder()
            .text(R.string.twitter)
            .icon(ContextCompat.getDrawable(context, R.drawable.ic_twitter))
            .setOnClickAction {
                ActivityHelper.startCustomTab(
                    this,
                    "https://twitter.com/benjsperry"
                )
            }
            .build())
            .addItem(MaterialAboutActionItem.Builder()
                .text(R.string.website)
                .icon(ContextCompat.getDrawable(context, R.drawable.ic_brower))
                .setOnClickAction {
                    ActivityHelper.startCustomTab(
                        this,
                        "https://iconscout.com/icon/octocat"
                    )
                }
                .build())
    }
}
