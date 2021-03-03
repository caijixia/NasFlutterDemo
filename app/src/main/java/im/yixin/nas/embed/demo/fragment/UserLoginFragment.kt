package im.yixin.nas.embed.demo.fragment

import android.os.Bundle
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import com.kaopiz.kprogresshud.KProgressHUD
import im.yixin.nas.embed.demo.R
import im.yixin.nas.embed.demo.impl.NasBridgeManager
import im.yixin.nas.embed.demo.util.ToastUtil
import im.yixin.nas.sdk.api.INasCallback
import im.yixin.nas.sdk.entity.UserToken
import java.util.regex.Pattern

/**
 * Created by jixia.cai on 2021/3/1 8:14 PM
 */
class UserLoginFragment : Fragment(R.layout.nas_demo_fragment_auth) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        findViews(view)
    }

    private fun findViews(view: View) {
        val et_mobile = view.findViewById<EditText>(R.id.et_mobile)
        val btn_auth = view.findViewById<View>(R.id.btn_auth)

        btn_auth.setOnClickListener {
            val mobile = et_mobile.text?.toString()
            if (mobile.isNullOrEmpty()) {
                ToastUtil.showToast(context!!, "手机号不能为空")
                return@setOnClickListener
            }
            if (!verifyMobile(mobile)) {
                ToastUtil.showToast(context!!, "手机号格式非法，请重新输入")
                return@setOnClickListener
            }
            showLoading()
            NasBridgeManager.instance.execTokenMock(mobile, object : INasCallback {
                override fun onSuccess(data: Any?) {
                    if (data is UserToken) {
                        //执行登录
                        startAuthLogin(data.accessToken, mobile)
                    }
                }

                override fun onError(code: Int, message: String?) {
                    ToastUtil.showToast(context!!, message)
                    hideLoading()
                }
            })
        }
    }

    private fun startAuthLogin(mobile: String?, token: String?) {
        NasBridgeManager.instance.execAuthLogin(mobile, token, object : INasCallback {
            override fun onSuccess(data: Any?) {
                ToastUtil.showToast(context!!, "授权登录成功 ~")
                hideLoading()
            }

            override fun onError(code: Int, message: String?) {
                ToastUtil.showToast(context!!, "授权登录失败 ~")
                hideLoading()
            }

        })
    }

    private fun verifyMobile(mobile: String?): Boolean {
        val pattern = Pattern.compile("^1[3|4|5|6|7|8][0-9]\\d{8}$")
        return pattern.matcher(mobile).matches()
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