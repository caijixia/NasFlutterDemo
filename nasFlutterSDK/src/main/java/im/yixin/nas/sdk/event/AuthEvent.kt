package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.NasBundle
import im.yixin.nas.sdk.NasMethodConst

/**
 * Created by jixia.cai on 2021/2/24 7:33 PM
 */
class AuthEvent(private val mobile: String?, private var token: String?) : BasicNasEvent() {

    override fun toBundle(): NasBundle {
        return NasBundle(
            method = NasMethodConst.EVENT_METHOD_AUTH,
            mapOf(
                "mobile" to mobile,
                "token" to token,
            )
        )
    }
}