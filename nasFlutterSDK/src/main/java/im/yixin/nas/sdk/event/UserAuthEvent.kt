package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.base.*
import im.yixin.nas.sdk.util.StringUtil

/**
 * Created by jixia.cai on 2021/3/1 9:54 AM
 */
class UserAuthEvent {

    companion object {

        private const val method = YXNasConstants.Method.EVENT_METHOD_AUTH
    }

    private class RequestBuilder(var mobile: String? = null, var token: String? = null) :
        IRequestEntryMap {
        override fun toMap(): Map<String, *>? {
            return mapOf("token" to token, "mobile" to mobile)
        }
    }

    class Request(private var mobile: String? = null, private var token: String? = null) :
        BaseNasRequest(method = method, RequestBuilder(mobile, token)) {

        override fun preCheck(): IRequestCheck.CheckResult? {
            if (StringUtil.checkMobile(mobile).not()) {
                return IRequestCheck.CheckResult(success = false, message = "mobile参数不合法")
            }
            if (token.isNullOrEmpty()) {
                return IRequestCheck.CheckResult(success = false, message = "token参数不能为空")
            }
            return null
        }
    }

    class Response private constructor(result: Result<Void>) :
        BaseNasResponse<Void>(method, result = result) {

        companion object {

            fun ensureResponse(result: Result<Void>? = null): Response {
                if (result == null) {
                    return Response(
                        Result.buildFailure(
                            YXNasConstants.ResultCode.CODE_RESULT_PARSE_ERROR,
                            "返回数据为空"
                        )
                    )
                }
                return Response(result)
            }
        }
    }
}