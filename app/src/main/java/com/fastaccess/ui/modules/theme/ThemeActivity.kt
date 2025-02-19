package com.fastaccess.ui.modules.theme

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewAnimationUtils
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.base.BaseActivity
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.modules.theme.fragment.ThemeFragmentMvp
import com.fastaccess.ui.widgets.CardsPagerTransformerBasic
import com.fastaccess.ui.widgets.ViewPagerView
import com.fastaccess.utils.setOnThrottleClickListener
import kotlin.math.hypot


/**
 * Created by Kosh on 08 Jun 2017, 10:34 PM
 */

class ThemeActivity : BaseActivity<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>(),
    ThemeFragmentMvp.ThemeListener {

    val pager: ViewPagerView by lazy { viewFind(R.id.pager)!! }
    val parentLayout: View by lazy { viewFind(R.id.parentLayout)!! }

    override fun layout(): Int = R.layout.theme_viewpager

    override val isTransparent: Boolean = false

    override fun canBack(): Boolean = true

    override val isSecured: Boolean = false

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> {
        return BasePresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pager.adapter =
            FragmentsPagerAdapter(supportFragmentManager, FragmentPagerAdapterModel.buildForTheme())
        pager.clipToPadding = false
        val partialWidth = resources.getDimensionPixelSize(R.dimen.spacing_s_large)
        val pageMargin = resources.getDimensionPixelSize(R.dimen.spacing_normal)
        val pagerPadding = partialWidth + pageMargin
        pager.pageMargin = pageMargin
        pager.setPageTransformer(true, CardsPagerTransformerBasic(4, 10))
        pager.setPadding(pagerPadding, pagerPadding, pagerPadding, pagerPadding)
        if (savedInstanceState == null) {
            val theme = PrefGetter.getThemeType(this)
            pager.setCurrentItem(theme - 1, true)
        }
    }

    override fun onChangePrimaryDarkColor(color: Int, darkIcons: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val view = window.decorView
            view.systemUiVisibility =
                if (darkIcons) View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR else view.systemUiVisibility and View
                    .SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
        }
        val cx = parentLayout.width / 2
        val cy = parentLayout.height / 2
        parentLayout.setBackgroundColor(color)
        window.statusBarColor = color
        changeNavColor(color)
    }

    private fun changeNavColor(color: Int) {
        window?.navigationBarColor = color
    }

    override fun onThemeApplied() {
        showMessage(R.string.success, R.string.change_theme_warning)
        onThemeChanged()
    }

}