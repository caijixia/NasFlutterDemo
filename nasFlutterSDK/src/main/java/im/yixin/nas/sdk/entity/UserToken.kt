package im.yixin.nas.sdk.entity

import com.google.gson.annotations.SerializedName
import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.event.base.IVerify

/**
 * Created by jixia.cai on 2021/3/1 5:51 PM
 */
data class UserToken(
    @SerializedName("access_token")
    var accessToken: String?,
    @SerializedName("refresh_token")
    var refreshToken: String?,
    var expire: Int?
) : IVerify {
    companion object {

        fun verify(result: UserToken?): Boolean {
            return result != null && result.verify()
        }
    }

    override fun verify(): Boolean {
        return IVerify.checkNotEmpty(accessToken) && IVerify.checkNotEmpty(refreshToken)// && expire != null
    }

    override fun toString(): String {
        return YXNasConstants.toJson(this) ?: ""
    }
}