package im.yixin.nas.sdk.event.callback

import im.yixin.nas.sdk.NasResultCode

/**
 * Created by jixia.cai on 2021/2/25 11:28 AM
 */
open class BasicEventResult<T> {
    var code: Int? = null
    var message: String? = null
    var data: T? = null

    fun success() = code == NasResultCode.CODE_SUCCESS

    companion object {

        fun <T> build(code: Int, message: String?, data: T?): BasicEventResult<T> {
            return BasicEventResult<T>().also {
                it.code = code
                it.message = message
                it.data = data
            }
        }

        fun <T> buildSuccess(data: T?, message: String? = null): BasicEventResult<T> {
            return BasicEventResult<T>().also {
                it.code = NasResultCode.CODE_SUCCESS
                it.message = message
                it.data = data
            }
        }

        fun buildFailure(
            code: Int = NasResultCode.CODE_BAD_REQUEST,
            message: String? = null
        ): BasicEventResult<Any> {
            return BasicEventResult<Any>().also {
                it.code = code
                it.message = message
            }
        }
    }
}