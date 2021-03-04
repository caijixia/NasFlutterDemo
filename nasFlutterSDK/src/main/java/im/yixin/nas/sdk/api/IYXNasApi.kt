package im.yixin.nas.sdk.api

import android.content.Intent
import androidx.fragment.app.Fragment
import im.yixin.nas.sdk.entity.UserInfo
import im.yixin.nas.sdk.entity.UserToken
import im.yixin.nas.sdk.event.base.BaseNasRequest
import im.yixin.nas.sdk.event.base.NasRequest
import im.yixin.nas.sdk.event.base.NasResponse

/**
 * Created by jixia.cai on 2021/2/22 9:53 AM
 */
interface IYXNasApi {

    fun obtainFlutterHost(): Fragment

    fun obtainFlutterIntent(): Intent

    fun setTokenRequestListener(listener: ITokenRequestListener?)

    fun requestUserInfo(callback: INasInvokeCallback<UserInfo>?)

    fun requestLoginStatus(callback: INasInvokeCallback<Boolean>?)

    fun authLogin(mobile: String?, token: String?, callback: INasInvokeCallback<Void>?)

    fun logout(callback: INasInvokeCallback<Void>?)
}

interface INasInvokeConnector {

    fun onResponse(response: NasResponse)

    fun onRequest(request: NasRequest, methodCall: IMethodCall<*>? = null)

    fun onBridgeConnected(bridge: INasChannelBridge)

    fun onBridgeDisconnected(bridge: INasChannelBridge)
}

interface INasChannelBridge {

    fun <T> invoke(request: BaseNasRequest, callback: INasInvokeCallback<T>? = null)
}

interface INasInvokeCallback<T> {

    fun onResult(code: Int, message: String?, data: T?)
}

interface ITokenRequestListener {

    fun onTokenRequest(methodCall: IMethodCall<UserToken>?)
}

interface IMethodCall<T> {

    fun success(result: T?)

    fun error(code: Int, message: String?)
}