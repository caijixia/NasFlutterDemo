package im.yixin.nas.sdk.api

import android.content.Intent
import androidx.fragment.app.Fragment
import im.yixin.nas.sdk.event.SDKInitEvent
import im.yixin.nas.sdk.event.base.BaseNasRequest
import im.yixin.nas.sdk.event.base.NasResponse

/**
 * Created by jixia.cai on 2021/2/22 9:53 AM
 */
interface IYXNasApi {

    fun obtainFlutterHost(): Fragment

    fun obtainFlutterIntent(): Intent
}

interface INasInvokeConnector {

    fun onResponse(response: NasResponse)

    fun onBridgeConnected(bridge: INasChannelBridge)

    fun onBridgeDisconnected(bridge: INasChannelBridge)
}

interface INasChannelBridge {

    fun invoke(request: BaseNasRequest, callback: INasCallback? = null)
}

interface INasCallback {

    fun onSuccess(data: Any? = null)

    fun onError(code: Int, message: String?)
}

interface INasInvokeCallback<T> {

    fun onResult(code: Int, message: String?, data: T?)
}