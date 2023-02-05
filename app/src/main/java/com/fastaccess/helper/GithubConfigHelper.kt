package com.fastaccess.helper

import com.fastaccess.BuildConfig

/**
 * Created by thermatk on 12.04.17.
 */
object GithubConfigHelper {
    @JvmStatic
    val redirectUrl = "octohub://login"

    @JvmStatic
    val clientId = BuildConfig.GITHUB_CLIENT_ID

    @JvmStatic
    val secret = BuildConfig.GITHUB_SECRET
}