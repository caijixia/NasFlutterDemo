package im.yixin.nas.sdk.entity

import im.yixin.nas.sdk.event.base.IVerify

/**
 * Created by jixia.cai on 2021/3/1 5:51 PM
 */
data class UserToken(
    var accessToken: String?,
    var refreshToken: String?,
    var expireIn: Int?
) : IVerify {
    companion object {

        fun verify(result: UserToken?): Boolean {
            return result != null && result.verify()
        }
    }

    override fun verify(): Boolean {
        return IVerify.checkNotEmpty(accessToken) && IVerify.checkNotEmpty(refreshToken) && expireIn != null
    }
}