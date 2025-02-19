package com.fastaccess.ui.base

import android.view.View
import android.widget.TextView
import com.fastaccess.R
import com.fastaccess.data.dao.FragmentPagerAdapterModel
import com.fastaccess.data.entity.Login
import com.fastaccess.data.entity.dao.LoginDao
import com.fastaccess.helper.PrefGetter
import com.fastaccess.ui.adapter.FragmentsPagerAdapter
import com.fastaccess.ui.widgets.AvatarLayout
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.ui.widgets.ViewPagerView
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout

/**
 * Created by Kosh on 09 Jul 2017, 3:50 PM
 */
class MainNavDrawer(val view: BaseActivity<*, *>, private val extraNav: NavigationView?) {
    init {
        setupView()
        val viewpager = view.findViewById<ViewPagerView>(R.id.drawerViewPager)
        viewpager?.let {
            it.adapter = FragmentsPagerAdapter(
                view.supportFragmentManager,
                FragmentPagerAdapterModel.buildForDrawer(view),
            )
            view.findViewById<TabLayout>(R.id.drawerTabLayout)?.setupWithViewPager(it)
        }
    }

    fun setupView() {
        val view = extraNav?.getHeaderView(0) ?: return
        val userModel: Login? = LoginDao.getUser().blockingGet().get()
        userModel?.let {
            (view.findViewById<View>(R.id.navAvatarLayout) as AvatarLayout).setUrl(
                it.avatarUrl, null, false,
                PrefGetter.isEnterprise
            )
            (view.findViewById<View>(R.id.navUsername) as TextView).text = it.login
            val navFullName = view.findViewById<FontTextView>(R.id.navFullName)
            when (it.name.isNullOrBlank()) {
                true -> navFullName.visibility = View.GONE
                else -> {
                    navFullName.visibility = View.VISIBLE
                    navFullName.text = it.name
                }
            }
        }
    }
}