package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.base.BaseNasRequest
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.Result

/**
 * Created by jixia.cai on 2021/3/1 9:54 AM
 */
class GetUserStatusEvent {

    companion object {

        private val method = YXNasConstants.Method.EVENT_METHOD_LOGIN_STATUS
    }

    class Request : BaseNasRequest {
        constructor() : super(GetUserStatusEvent.method, arguments = null)
    }

    class Response : BaseNasResponse<Boolean> {

        private constructor(result: Result<Boolean>) : super(
            method = GetUserStatusEvent.method,
            result = result
        )

        companion object {

            fun ensureResponse(result: Result<Boolean>? = null): Response {
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