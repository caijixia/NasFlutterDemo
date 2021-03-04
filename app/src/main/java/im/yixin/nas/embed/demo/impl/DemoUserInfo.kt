package im.yixin.nas.embed.demo.impl

import im.yixin.nas.sdk.entity.UserToken

/**
 * Created by jixia.cai on 2021/3/3 7:34 PM
 */
data class DemoUserInfo(var mobile: String, var isLogin: Boolean?, var token: UserToken?) {

    fun withToken(userToken: UserToken?): DemoUserInfo {
        this.token = token
        return this
    }
}