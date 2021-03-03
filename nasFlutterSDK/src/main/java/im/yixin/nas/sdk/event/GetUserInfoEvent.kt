package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.entity.UserInfo
import im.yixin.nas.sdk.event.base.*
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/3/1 9:55 AM
 */
class GetUserInfoEvent {

    companion object {

        private val method = YXNasConstants.Method.EVENT_METHOD_LOGIN_INFO
    }

    class Request : BaseNasRequest(method = YXNasConstants.Method.EVENT_METHOD_LOGIN_INFO)

    class Response : BaseNasResponse<UserInfo> {

        private constructor(result: Result<UserInfo>?) : super(
            method = GetUserInfoEvent.method,
            result = result
        )

        companion object {

            fun ensureResponse(result: Result<UserInfo>? = null): Response {
                if (result == null) return Response(
                    result = Result.buildFailure(
                        code = YXNasConstants.ResultCode.CODE_RESULT_PARSE_ERROR,
                        message = "返回数据为空"
                    )
                )
                return Response(result)
            }
        }
    }
}