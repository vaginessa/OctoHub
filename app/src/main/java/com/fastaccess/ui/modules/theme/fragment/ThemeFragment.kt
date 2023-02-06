package com.fastaccess.ui.modules.theme.fragment

import android.app.Activity
import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.ContextThemeWrapper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.Toolbar
import com.fastaccess.R
import com.fastaccess.helper.*
import com.fastaccess.ui.base.BaseFragment
import com.fastaccess.ui.widgets.SpannableBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * Created by Kosh on 08 Jun 2017, 10:53 PM
 */

class ThemeFragment : BaseFragment<ThemeFragmentMvp.View, ThemeFragmentPresenter>(),
    ThemeFragmentMvp.View {

    lateinit var apply: FloatingActionButton
    lateinit var toolbar: Toolbar

    private val mTheme = "appTheme"
    private var primaryDarkColor: Int = 0
    private var theme: Int = 0
    private var themeListener: ThemeFragmentMvp.ThemeListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        themeListener = context as ThemeFragmentMvp.ThemeListener
    }

    override fun onDetach() {
        themeListener = null
        super.onDetach()
    }

    override fun fragmentLayout(): Int = 0

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        apply = view.findViewById(R.id.apply)
        toolbar = view.findViewById(R.id.toolbar)
        apply.setOnClickListener {
            setTheme()
        }
        toolbar.setNavigationOnClickListener { activity?.onBackPressed() }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        theme = requireArguments().getInt(BundleConstant.ITEM)
        val contextThemeWrapper = ContextThemeWrapper(activity, theme)
        primaryDarkColor = ViewHelper.getPrimaryDarkColor(contextThemeWrapper)
        val localInflater = inflater.cloneInContext(contextThemeWrapper)
        return localInflater.inflate(R.layout.theme_layout, container, false)!!
    }

    override fun providePresenter(): ThemeFragmentPresenter {
        return ThemeFragmentPresenter()
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            if (themeListener != null) {
                themeListener!!.onChangePrimaryDarkColor(
                    primaryDarkColor,
                    theme == R.style.ThemeLight
                )
            }
        }
    }

    companion object {
        fun newInstance(style: Int): ThemeFragment {
            val fragment = ThemeFragment()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ITEM, style)
                .end()
            return fragment
        }
    }

    private fun setTheme() {
        when (theme) {
            R.style.ThemeLight -> setTheme(getString(R.string.light_theme_mode))
            R.style.ThemeDark -> setTheme(getString(R.string.dark_theme_mode))
            R.style.ThemeAmlod -> setTheme(getString(R.string.amlod_theme_mode))
            R.style.ThemeBluish -> setTheme(getString(R.string.bluish_theme))
            R.style.ThemeMidnight -> setTheme(getString(R.string.mid_night_blue_theme_mode))
        }
    }

    private fun setTheme(theme: String) {
        PrefHelper.putAny(mTheme, theme)
        themeListener?.onThemeApplied()
    }
}
