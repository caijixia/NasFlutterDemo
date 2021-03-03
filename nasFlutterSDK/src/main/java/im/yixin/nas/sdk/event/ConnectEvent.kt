package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.base.BaseNasResponse
import im.yixin.nas.sdk.event.base.Result

/**
 * Created by jixia.cai on 2021/3/1 4:51 PM
 */
class ConnectEvent {

    class Response : BaseNasResponse<Void> {

        constructor(result: Result<Void>?) : super(
            method = YXNasConstants.Method.EVENT_METHOD_INNER_CONNECT,
            result = result
        )
    }
}