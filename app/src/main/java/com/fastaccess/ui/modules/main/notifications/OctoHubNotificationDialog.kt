package com.fastaccess.ui.modules.main.notifications

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import androidx.fragment.app.FragmentManager
import com.fastaccess.R
import com.fastaccess.data.entity.OctoHubNotification
import com.fastaccess.data.entity.OctoHubNotification.NotificationType
import com.fastaccess.data.entity.dao.OctoHubNotificationDao
import com.fastaccess.helper.BundleConstant
import com.fastaccess.helper.Bundler
import com.fastaccess.helper.PrefGetter
import com.fastaccess.helper.RxHelper
import com.fastaccess.ui.base.BaseDialogFragment
import com.fastaccess.ui.base.mvp.BaseMvp
import com.fastaccess.ui.base.mvp.presenter.BasePresenter
import com.fastaccess.ui.widgets.FontTextView
import com.fastaccess.utils.Optional
import com.fastaccess.utils.setOnThrottleClickListener
import io.reactivex.Observable
import io.reactivex.disposables.Disposable

/**
 * Created by Kosh on 17.11.17.
 */
class OctoHubNotificationDialog :
    BaseDialogFragment<BaseMvp.FAView, BasePresenter<BaseMvp.FAView>>() {

    init {
        suppressAnimation = true
        isCancelable = false
    }

    private val model by lazy {
        arguments?.getParcelable<OctoHubNotification>(BundleConstant.ITEM)
    }

    override fun onFragmentCreated(view: View, savedInstanceState: Bundle?) {
        view.findViewById<View>(R.id.cancel).setOnThrottleClickListener {
            dismiss()
        }
        val title = view.findViewById<FontTextView>(R.id.title)
        val description = view.findViewById<FontTextView>(R.id.description)
        model?.let {
            title?.text = it.title
            description?.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(it.body, Html.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(it.body)
            }
            it.read = true
            presenter.manageObservable(
                OctoHubNotificationDao.update(it).toObservable()
            )
        } ?: dismiss()
    }

    override fun providePresenter(): BasePresenter<BaseMvp.FAView> = BasePresenter()

    override fun fragmentLayout(): Int = R.layout.dialog_guide_layout

    companion object {
        @JvmStatic
        private val TAG = OctoHubNotificationDialog::class.java.simpleName

        fun newInstance(model: OctoHubNotification): OctoHubNotificationDialog {
            val fragment = OctoHubNotificationDialog()
            fragment.arguments = Bundler.start()
                .put(BundleConstant.ITEM, model)
                .end()
            return fragment
        }

        fun show(fragmentManager: FragmentManager, model: OctoHubNotification? = null): Disposable {
            val notificationObservable = if (model != null)
                Observable.just(Optional.ofNullable(model)) else
                OctoHubNotificationDao.getLatest().toObservable()
            val disposable = RxHelper.getObservable(
                notificationObservable
                    .flatMap {
                        var observable = Observable.just(0L)
                        if (!it.isEmpty()) {
                            val notification = it.or()
                            newInstance(notification).show(fragmentManager, TAG)
                        }
                        observable
                    }).subscribe({

            }) { obj: Throwable ->
                obj.printStackTrace()
            }
            return disposable
        }

        fun show(fragmentManager: FragmentManager): Disposable {
            return show(fragmentManager, null)
        }
    }
}
