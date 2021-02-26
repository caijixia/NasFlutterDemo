package im.yixin.nas.sdk.event.convert

import im.yixin.nas.sdk.NasMethodConst
import im.yixin.nas.sdk.event.callback.VoidResult

/**
 * Created by jixia.cai on 2021/2/25 2:40 PM
 */
class InitEventBundle : EventBundle {

    constructor(result: VoidResult?) : super(method = NasMethodConst.EVENT_METHOD_SDK_INIT, result)
}