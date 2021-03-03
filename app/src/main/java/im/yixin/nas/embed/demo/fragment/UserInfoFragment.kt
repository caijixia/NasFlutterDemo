package im.yixin.nas.embed.demo.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import im.yixin.nas.embed.demo.R
import im.yixin.nas.embed.demo.impl.NasBridgeManager
import im.yixin.nas.embed.demo.util.ToastUtil
import im.yixin.nas.sdk.api.INasCallback
import im.yixin.nas.sdk.entity.UserInfo

/**
 * Created by jixia.cai on 2021/3/1 7:49 PM
 */
class UserInfoFragment : Fragment(R.layout.nas_demo_fragment_user_info) {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = super.onCreateView(inflater, container, savedInstanceState)
        if (view != null) initViews(view)
        return view
    }

    private fun initViews(view: View) {
        val btn_use_info = view.findViewById<View>(R.id.btn_use_info)
        val btn_login_status = view.findViewById<View>(R.id.btn_login_status)
        val btn_quit = view.findViewById<View>(R.id.btn_quit)
        btn_use_info.setOnClickListener {
            showLoading()
            NasBridgeManager.instance.getCurrentUserInfo(object : INasCallback {
                override fun onSuccess(data: Any?) {
                    hideLoading()
                    if (data is UserInfo?) {
                        ToastUtil.showToast(context!!, "当前用户信息: $data")
                    }
                }

                override fun onError(code: Int, message: String?) {
                    hideLoading()
                    ToastUtil.showToast(context!!, "获取用户信息失败: code:$code, message:$message")
                }

            })
        }

        btn_login_status.setOnClickListener {
            showLoading()
            NasBridgeManager.instance.getLoginStatus(object : INasCallback {
                override fun onSuccess(data: Any?) {
                    hideLoading()
                    if (data is Boolean) {
                        ToastUtil.showToast(context!!, if (data) "当前已登录" else "当前未登录")
                    }
                }

                override fun onError(code: Int, message: String?) {
                    hideLoading()
                    ToastUtil.showToast(context!!, "获取用户登录信息失败: code:$code, message:$message")
                }

            })
        }

        btn_quit.setOnClickListener {
            showLoading()
            NasBridgeManager.instance.execUserLogout(object : INasCallback {
                override fun onSuccess(data: Any?) {
                    hideLoading()
                    ToastUtil.showToast(context!!, "用户退出登录")
                }

                override fun onError(code: Int, message: String?) {
                    hideLoading()
                    ToastUtil.showToast(context!!, "获取用户退出失败: code:$code, message:$message")
                }

            })
        }
    }

    private var _loadingDialog: KProgressHUD? = null

    private fun showLoading() {
        _loadingDialog = KProgressHUD.create(context!!).also {
            it.setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
            it.setCancellable(true)
            it.setDimAmount(0.5f)
        }.show()
    }

    private fun hideLoading() {
        _loadingDialog?.dismiss()
    }
}