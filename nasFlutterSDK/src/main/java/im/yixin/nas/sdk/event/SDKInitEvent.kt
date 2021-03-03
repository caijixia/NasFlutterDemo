package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.entity.UserToken
import im.yixin.nas.sdk.event.base.BaseNasRequest
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.IRequestEntrySet
import im.yixin.nas.sdk.event.base.Result
import im.yixin.nas.sdk.event.base.VoidResult
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/3/1 9:54 AM
 */
class SDKInitEvent {

    companion object {

        private val method = YXNasConstants.Method.EVENT_METHOD_SDK_INIT
    }

    class Request : BaseNasRequest {

        constructor(appkey: String, appsecret: String) : super(
            SDKInitEvent.method,
            RequestBuilder(appkey, appsecret)
        )
    }

    private class RequestBuilder(var appkey: String, var appsecret: String) : IRequestEntrySet {
        override fun toJSON(): JSONObject? {
            val map = mapOf("appkey" to appkey, "appsecret" to appsecret)
            return JSONObject(map)
        }
    }

    class Response private constructor(result: Result<Void>) :
        BaseNasResponse<Void>(method = method, result) {

        companion object {

            fun buildSuccess(): Response {
                return Response(
                    result = VoidResult(
                        code = YXNasConstants.ResultCode.CODE_SUCCESS,
                        message = "调用成功"
                    )
                )
            }

            fun build(code: Int, message: String?) = Response(VoidResult(code, message))

            fun ensureResponse(result: Result<Void>? = null): Response {
                if (result == null) {
                    return build(YXNasConstants.ResultCode.CODE_RESULT_PARSE_ERROR, "返回数据为空")
                }
                return Response(result = result)
            }
        }
    }
}