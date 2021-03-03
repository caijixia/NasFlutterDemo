package im.yixin.nas.sdk.const

import com.google.gson.GsonBuilder

/**
 * Created by jixia.cai on 2021/3/1 9:53 AM
 */
object YXNasConstants {

    val sGson = GsonBuilder().create()

    object Method {

        const val EVENT_METHOD_SDK_INIT = "sdk_init"

        const val EVENT_METHOD_AUTH = "auth"

        const val EVENT_METHOD_LOGOUT = "logout"

        const val EVENT_METHOD_SWITCH = "switch" //暂时不用

        //其他sdk 交互 event 待补充，如：获取登录态/登录信息等
        const val EVENT_METHOD_LOGIN_STATUS = "login_status"

        const val EVENT_METHOD_LOGIN_INFO = "login_info"

        const val EVENT_METHOD_MOCK_TOKEN = "mock_token"

        //内部事件
        const val EVENT_METHOD_INNER_CONNECT = "bridge_connect"

        const val EVENT_NAME_BRIDGE_DISCONNECT = "bridge_disconnect"
    }

    //统一错误码
    object ResultCode {

        const val CODE_SUCCESS = 200

        const val CODE_BAD_REQUEST = 400 //请求错误

        const val CODE_INTERRUPT = 4001 //调用中断

        const val CODE_RESULT_PARSE_ERROR = 4002 //返回result数据解析异常
    }

    fun compose(tag: String?): String {
        return "native${if (tag.isNullOrEmpty()) "" else " : $tag"}"
    }

    fun toJson(any: Any?): String? {
        return try {
            sGson.toJson(any)
        } catch (ex: Exception) {
            ex.printStackTrace()
            null
        }
    }
}