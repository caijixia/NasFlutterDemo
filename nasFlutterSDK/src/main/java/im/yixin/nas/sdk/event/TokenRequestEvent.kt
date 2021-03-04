package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.base.BaseNasResponse

/**
 * Created by jixia.cai on 2021/3/3 10:54 AM
 */
class TokenRequestEvent {

    companion object {

        private const val method = YXNasConstants.Method.EVENT_METHOD_TOKEN_REQUEST
    }

    class Request : BaseNasResponse<Void> {

        constructor() : super(TokenRequestEvent.method, null)
    }
}