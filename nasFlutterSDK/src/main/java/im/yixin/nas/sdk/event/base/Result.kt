package im.yixin.nas.sdk.event.base

import im.yixin.nas.sdk.const.YXNasConstants
import org.json.JSONObject

/**
 * Created by jixia.cai on 2021/2/25 11:28 AM
 */
open class Result<T> : IRequestEntrySet {

    var code: Int = YXNasConstants.ResultCode.CODE_SUCCESS
    var message: String? = null
    var data: T? = null

    fun success() = code == YXNasConstants.ResultCode.CODE_SUCCESS

    companion object {

        fun <T> build(code: Int, message: String?, data: T? = null): Result<T> {
            return Result<T>().also {
                it.code = code
                it.message = message
                it.data = data
            }
        }

        fun <T> buildSuccess(data: T? = null, message: String? = null): Result<T> {
            return Result<T>().also {
                it.code = YXNasConstants.ResultCode.CODE_SUCCESS
                it.message = message
                it.data = data
            }
        }

        fun <T> buildFailure(
            code: Int = YXNasConstants.ResultCode.CODE_BAD_REQUEST,
            message: String? = null
        ): Result<T> {
            return Result<T>().also {
                it.code = code
                it.message = message
            }
        }

        fun verify(result: Result<*>?): Boolean {
            if (result == null) return false
            return true
        }
    }

    override fun toJSON(): JSONObject? {
        return JSONObject().also {
            it.put("code", code)
            it.put("message", message)
            it.put("data", data)
        }
    }

}

class VoidResult : Result<Void> {

    constructor(code: Int, message: String? = null) {
        this.code = code
        this.message = message
    }
}