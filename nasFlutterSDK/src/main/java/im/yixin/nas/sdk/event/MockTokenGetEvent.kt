package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.entity.UserToken
import im.yixin.nas.sdk.event.base.*
import im.yixin.nas.sdk.util.StringUtil
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/3/1 9:52 AM
 */
class MockTokenGetEvent {

    companion object {

        private var method = YXNasConstants.Method.EVENT_METHOD_MOCK_TOKEN
    }

    class Request : BaseNasRequest {

        private var mobile: String? = null

        constructor(mobile: String? = null) : super(
            method = MockTokenGetEvent.method,
            arguments = RequestBuilder(mobile)
        ) {
            this.mobile = mobile
        }

        override fun preCheck(): IRequestCheck.CheckResult? {
            if (mobile.isNullOrEmpty()) {
                return IRequestCheck.CheckResult(success = false, message = "手机号不能为空")
            }

            if (StringUtil.checkMobile(mobile).not()) {
                return IRequestCheck.CheckResult(success = false, message = "手机号格式非法")
            }
            return null
        }
    }

    class Response : BaseNasResponse<UserToken> {

        constructor(result: Result<UserToken>? = null) : super(
            MockTokenGetEvent.method,
            result = result
        )

        companion object {
            fun ensureResponse(result: Result<UserToken>? = null): Response {
                //判断异常场景，并拦截返回result

                // 返回为null
                if (result == null) {
                    return Response(
                        result = Result.buildFailure(
                            code = YXNasConstants.ResultCode.CODE_RESULT_PARSE_ERROR,
                            message = "返回数据为空"
                        )
                    )
                }
                //返回成功，但result.data数据非法，解析为Response.4002进行返回
                if (result.success() && UserToken.verify(result.data).not()) {
                    return Response(
                        result = Result.buildFailure(
                            code = YXNasConstants.ResultCode.CODE_RESULT_PARSE_ERROR,
                            message = "返回的result.data不合法"
                        )
                    )
                }
                return Response(result = result)
            }

        }
    }

    private class RequestBuilder(var mobile: String?) : IRequestEntrySet {
        override fun toJSON(): JSONObject? {
            val map = mapOf("mobile" to mobile)
            return JSONObject(map)
        }
    }
}