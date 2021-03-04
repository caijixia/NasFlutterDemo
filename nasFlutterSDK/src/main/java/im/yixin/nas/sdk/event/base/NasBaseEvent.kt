package im.yixin.nas.sdk.event.base

import im.yixin.nas.sdk.const.YXNasConstants
import im.yixin.nas.sdk.entity.UserInfo
import im.yixin.nas.sdk.entity.UserToken
import im.yixin.nas.sdk.error.NasArgumentException
import im.yixin.nas.sdk.event.*
import im.yixin.nas.sdk.util.ParseUtil
import org.json.JSONObject
import java.lang.Exception

/**
 * Created by jixia.cai on 2021/3/1 9:56 AM
 */
abstract class BaseNasRequest : IRequestCheck {

    var method: String = ""
    var arguments: Any? = null

    private val EMPTY = NasRequest(method = "")

    constructor(method: String, arguments: Any? = null) {
        this.method = method
        this.arguments = arguments
    }

    private fun toBundle(): NasRequest? {
        if (method.isNullOrEmpty()) return EMPTY

        //判断arguments类型
        if (arguments is IRequestEntrySet) {
            return NasRequest(method, (arguments!! as IRequestEntrySet).toJSON().toString())
        } else if (arguments is JSONObject) {
            return NasRequest(method, arguments.toString())
        } else if (arguments != null) {
            throw NasArgumentException("Type of arguments is illegal, and must be type of IRequestEntrySet or JSONObject ~")
        }
        return NasRequest(method)
    }

    fun toJSON(): JSONObject {
        return toBundle()?.toJSON() ?: NasRequest(method).toJSON()
    }

    override fun preCheck(): IRequestCheck.CheckResult? {
        return null
    }
}

interface IRequestCheck {

    fun preCheck(): CheckResult?

    data class CheckResult(val success: Boolean = true, val message: String? = null)
}

interface IRequestEntrySet {

    fun toJSON(): JSONObject? //可以传递复杂类型的参数
}

interface IRequestEntryMap : IRequestEntrySet {

    fun toMap(): Map<String, *>?

    override fun toJSON(): JSONObject? {
        return JSONObject(toMap() ?: mapOf<String, Any>())
    }
}

data class NasRequest(val method: String, val arguments: Any? = null) {

    fun toJSON(): JSONObject {
        return JSONObject(YXNasConstants.toJson(this))
    }
}

data class NasResponse(val method: String, val result: Any? = null) {

    fun isEmpty(): Boolean = method.isEmpty()

    override fun toString(): String {
        return YXNasConstants.toJson(this) ?: ""
    }
}

open class BaseNasResponse<T> protected constructor(var method: String?, var result: Result<T>?) {

    companion object Factory {

        @JvmStatic
        fun parse(response: NasResponse?): BaseNasResponse<*> {
            if (response == null || response.isEmpty()) {
                return EmptyResponse(ParseUtil.parseObject<Result<Void>>(response?.result))
            }
            when (response.method) {
                YXNasConstants.Method.EVENT_METHOD_INNER_CONNECT -> {
                    val result = ParseUtil.parseVoidResult(response.result)
                    return ConnectEvent.Response(result)
                }
                YXNasConstants.Method.EVENT_METHOD_SDK_INIT -> {
                    val result = ParseUtil.parseVoidResult(response.result)
                    return SDKInitEvent.Response.ensureResponse(result)
                }
                YXNasConstants.Method.EVENT_METHOD_MOCK_TOKEN -> {
                    val result = ParseUtil.parseObject<Result<UserToken>>(
                        response.result
                    )
                    return MockTokenGetEvent.Response.ensureResponse(result)
                }
                YXNasConstants.Method.EVENT_METHOD_AUTH -> {
                    val result = ParseUtil.parseVoidResult(response.result)
                    return UserAuthEvent.Response.ensureResponse(result)
                }
                YXNasConstants.Method.EVENT_METHOD_LOGIN_INFO -> {
                    val result = ParseUtil.parseObject<Result<UserInfo>>(response.result)
                    return GetUserInfoEvent.Response.ensureResponse(result)
                }
                YXNasConstants.Method.EVENT_METHOD_LOGIN_STATUS -> {
                    val result = ParseUtil.parseObject<Result<Boolean>>(response.result)
                    return GetUserStatusEvent.Response.ensureResponse(result)
                }
                YXNasConstants.Method.EVENT_METHOD_TOKEN_REQUEST -> {
                    return TokenRequestEvent.Request()
                }
                YXNasConstants.Method.EVENT_METHOD_LOGOUT -> {
                    val result = ParseUtil.parseVoidResult(response.result)
                    return UserLogoutEvent.Response.ensureResponse(result)
                }
            }
            return MethodIllegalResponse(
                method = response.method,
                ParseUtil.parseObject<Result<Void>>(response.result)
            )
        }
    }

    val data: T?
        get() {
            return result?.data
        }

    val code: Int
        get() {
            return result?.code ?: YXNasConstants.ResultCode.CODE_BAD_REQUEST
        }

    val success: Boolean
        get() {
            return result?.success() ?: false
        }

    val bundle: NasResponse
        get() {
//            var jobject: JSONObject? = null
//            if (result != null) {
//                try {
//                    jobject = JSONObject(YXNasConstants.toJson(result))
//                } catch (ex: Exception) {
//                    ex.printStackTrace()
//                }
//            }
            return NasResponse(method!!, result)
        }

    override fun toString(): String {
        return YXNasConstants.toJson(this) ?: ""
    }
}

class EmptyResponse : BaseNasResponse<Void> {

    constructor(result: Result<Void>?) : super(method = "", result = result)
}

class MethodIllegalResponse : BaseNasResponse<Void> {

    constructor(method: String, result: Result<Void>?) : super(method, result)
}

interface IVerify {

    fun verify(): Boolean

    companion object {
        fun checkNotEmpty(value: String?): Boolean = !value.isNullOrEmpty()
    }
}