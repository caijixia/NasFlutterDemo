package im.yixin.nas.sdk.entity

import im.yixin.nas.sdk.const.YXNasConstants

/**
 * Created by jixia.cai on 2021/3/1 5:50 PM
 */
data class UserInfo(var mobile: String?, var token: String?, var avatarUrl: String?) {

    override fun toString(): String {
        return YXNasConstants.toJson(this) ?: ""
    }
}