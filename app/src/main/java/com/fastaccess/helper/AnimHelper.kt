package com.fastaccess.helper

import android.R
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ObjectAnimator
import android.app.Dialog
import android.view.View
import android.view.ViewAnimationUtils
import android.view.ViewTreeObserver
import android.view.animation.AccelerateInterpolator
import android.view.animation.Interpolator
import android.view.animation.LinearInterpolator
import android.widget.PopupWindow
import androidx.annotation.UiThread
import androidx.core.view.ViewCompat
import androidx.fragment.app.DialogFragment
import androidx.interpolator.view.animation.FastOutLinearInInterpolator
import androidx.interpolator.view.animation.LinearOutSlowInInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton.OnVisibilityChangedListener
import kotlin.math.hypot
import kotlin.math.sqrt

/**
 * Created by Kosh on 27 May 2016, 9:04 PM
 */
object AnimHelper {
    private val FAST_OUT_LINEAR_IN_INTERPOLATOR: Interpolator = FastOutLinearInInterpolator()
    private val LINEAR_OUT_SLOW_IN_INTERPOLATOR: Interpolator = LinearOutSlowInInterpolator()
    private val interpolator: Interpolator = LinearInterpolator()


    @JvmStatic
    @UiThread
    fun animateVisibility(view: View?, show: Boolean) {
        if (view == null) {
            return
        }
        if (!ViewCompat.isAttachedToWindow(view)) {
            view.viewTreeObserver.addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    view.viewTreeObserver.removeOnPreDrawListener(this)
                    animateSafeVisibility(show, view)
                    return true
                }
            })
        } else {
            animateSafeVisibility(show, view)
        }
    }

    @UiThread
    fun animateSafeVisibility(show: Boolean, view: View) {
        val visibility = View.GONE
        view.animate().cancel()
        val animator = view.animate().setDuration(200).alpha(if (show) 1f else 0f)
            .setInterpolator(AccelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationStart(animation: Animator) {
                    super.onAnimationStart(animation)
                    if (show) {
                        view.scaleX = 1f
                        view.scaleY = 1f
                        view.visibility = View.VISIBLE
                    }
                }

                override fun onAnimationEnd(animation: Animator) {
                    super.onAnimationEnd(animation)
                    if (!show) {
                        view.visibility = visibility
                        view.scaleX = 0f
                        view.scaleY = 0f
                    }
                    animation.removeListener(this)
                    view.clearAnimation()
                }
            })
        val x: Float = if (show) 1F else 0F
        animator.scaleX(x).scaleY(x)
    }

    @JvmStatic
    @UiThread
    fun mimicFabVisibility(
        show: Boolean, view: View,
        listener: OnVisibilityChangedListener?
    ) {
        if (show) {
            view.animate().cancel()
            if (ViewCompat.isLaidOut(view)) {
                if (view.visibility != View.VISIBLE) {
                    view.alpha = 0f
                    view.scaleY = 0f
                    view.scaleX = 0f
                }
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .alpha(1f)
                    .setDuration(200)
                    .setInterpolator(LINEAR_OUT_SLOW_IN_INTERPOLATOR)
                    .withStartAction {
                        view.visibility = View.VISIBLE
                        listener?.onShown(null)
                    }
            } else {
                view.visibility = View.VISIBLE
                view.alpha = 1f
                view.scaleY = 1f
                view.scaleX = 1f
                listener?.onShown(null)
            }
        } else {
            view.animate()
                .scaleX(0f)
                .scaleY(0f)
                .alpha(0f)
                .setDuration(40).interpolator = FAST_OUT_LINEAR_IN_INTERPOLATOR
            view.visibility = View.GONE
            listener?.onHidden(null)
        }
    }
}