package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.base.BaseNasRequest
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.VoidResult

/**
 * Created by jixia.cai on 2021/3/1 9:54 AM
 */
class UserLogoutEvent {

    companion object {
        private val method = YXNasConstants.Method.EVENT_METHOD_LOGOUT
    }

    class Request : BaseNasRequest(method = method)

    class Response(result: VoidResult) : BaseNasResponse<Void>(method, result = result)
}