package im.yixin.nas.embed.demo.impl

import android.content.Context
import im.yixin.nas.embed.demo.util.ToastUtil
import im.yixin.nas.sdk.api.INasCallback
import im.yixin.nas.sdk.api.INasChannelBridge
import im.yixin.nas.sdk.event.*

/**
 * Created by jixia.cai on 2021/3/1 8:12 PM
 */
class NasBridgeManager {

    companion object {
        val instance = NasBridgeManager()
    }

    private var connected = false

    private var initialized = false

    var context: Context? = null

    private var _bridge: INasChannelBridge? = null

    fun setupBridge(bridge: INasChannelBridge) {
        _bridge = bridge
        connected = true
    }

    fun disconnectBridge(bridge: INasChannelBridge) {
        _bridge = bridge
        connected = false
    }

    fun getCurrentUserInfo(callback: INasCallback?) {
        if (assertInit()) {
            _bridge?.invoke(GetUserInfoEvent.Request(), object : INasCallback {
                override fun onSuccess(data: Any?) {
                    callback?.onSuccess()
                }

                override fun onError(code: Int, message: String?) {
                    callback?.onError(code, message)
                }

            })
        }
    }

    fun getLoginStatus(callback: INasCallback?) {
        if (assertInit()) {
            _bridge?.invoke(GetUserStatusEvent.Request(), object : INasCallback {
                override fun onSuccess(data: Any?) {
                    callback?.onSuccess(data)
                }

                override fun onError(code: Int, message: String?) {
                    callback?.onError(code, message)
                }
            })
        }
    }

    fun execTokenMock(mobile: String?, callback: INasCallback?) {
        if (assertInit()) {
            _bridge?.invoke(MockTokenGetEvent.Request(mobile), object : INasCallback {
                override fun onSuccess(data: Any?) {
                    callback?.onSuccess(data)
                }

                override fun onError(code: Int, message: String?) {
                    callback?.onError(code, message)
                }
            })
        }
    }

    fun execAuthLogin(mobile: String?, token: String?, callback: INasCallback?) {
        if (assertInit()) {
            _bridge?.invoke(
                UserAuthEvent.Request(mobile = mobile, token = token),
                object : INasCallback {
                    override fun onSuccess(data: Any?) {
                        callback?.onSuccess(data)
                        if (data is Boolean && data) {
                            notifyUserAuthSuccess()
                        }
                    }

                    override fun onError(code: Int, message: String?) {
                        callback?.onError(code, message)
                    }
                })
        }
    }

    fun execUserLogout(callback: INasCallback?) {
        if (assertInit()) {
            _bridge?.invoke(UserLogoutEvent.Request(), object : INasCallback {
                override fun onSuccess(data: Any?) {
                    callback?.onSuccess()
                    if (data is Boolean && data) {
                        notifyUserLogoutSuccess()
                    }
                }

                override fun onError(code: Int, message: String?) {
                }

            })
        }
    }

    private fun assertInit(): Boolean {
        if (connected.not()) {
            ToastUtil.showToast(context!!, "nas-bridge未连接")
            return false
        }
        if (initialized.not()) {
            ToastUtil.showToast(context!!, "nas-sdk未初始化")
            return false
        }
        return true
    }

    val _listeners = mutableListOf<OnNasInitListener>()

    fun addNasEventListener(listener: OnNasInitListener?, sticky: Boolean = true) {
        if (listener != null && !_listeners.contains(listener)) {
            if (sticky && _initEventResponse?.success == true) {
                listener.onSDKInitSuccess()
            }
            _listeners.add(listener)
        }
    }

    fun removeNasEventListener(listener: OnNasInitListener?) {
        if (_listeners.contains(listener)) {
            _listeners.remove(listener)
        }
    }

    private var _initEventResponse: SDKInitEvent.Response? = null

    fun notifyInitEventResponse(response: SDKInitEvent.Response) {
        if (response.success) {
            initialized = true
        }
        _initEventResponse = response
        _listeners.forEach {
            if (_initEventResponse?.success == true) {
                it.onSDKInitSuccess()
            }
        }
    }

    private var _userListener: OnNasUserListener? = null

    fun setNasUserListener(listener: OnNasUserListener?) {
        _userListener = listener
    }

    fun notifyUserAuthSuccess() {
        _userListener?.onUserAuthSuccess()
    }

    fun notifyUserLogoutSuccess() {
        _userListener?.onUserLogoutSuccess()
    }
}

interface OnNasInitListener {

    fun onSDKInitSuccess()
}

interface OnNasUserListener {

    fun onUserAuthSuccess()

    fun onUserLogoutSuccess()
}