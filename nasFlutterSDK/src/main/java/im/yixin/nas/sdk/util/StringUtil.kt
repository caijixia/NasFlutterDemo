package im.yixin.nas.sdk.util

/**
 * Created by jixia.cai on 2021/3/1 4:31 PM
 */
object StringUtil {

    fun checkMobile(mobile: String?): Boolean {
        return checkNotEmpty(mobile)
    }

    fun checkNotEmpty(input: String?): Boolean {
        return !input.isNullOrEmpty()
    }
}