package im.yixin.nas.sdk.util

import android.text.TextUtils
import android.util.Log
import im.yixin.nas.sdk.YXNasSDK
import java.util.concurrent.ConcurrentHashMap

/**
 * Created by jixia.cai on 2021/2/20 9:38 AM
 */
object LogUtil {

    @JvmField
    var isLoggable = true

    var LOGGER_TAG_DEFAULT: String = YXNasSDK.TAG

    private var sLoggerFactory: LoggerFactory = object : LoggerFactory {
        private val loggerMap: ConcurrentHashMap<String?, Logger?> =
            ConcurrentHashMap<String?, Logger?>()

        override fun provideLogger(tag: String?): Logger {
            if (!loggerMap.contains(tag) && loggerMap[tag] == null) {
                val logger = Logger(tag)
                loggerMap[tag] = logger
            }
            return loggerMap[tag]!!
        }
    }
    private var DEFAULT_LOGGER: Logger = sLoggerFactory.provideLogger(LOGGER_TAG_DEFAULT)

    fun getLogger(tag: String?): Logger {
        return sLoggerFactory.provideLogger(tag)
    }

    fun i(tag: String?, message: String?) {
        getLogger(tag).i(message)
    }

    fun i(message: String?) {
        DEFAULT_LOGGER.i(message)
    }

    fun d(tag: String?, message: String?) {
        getLogger(tag).d(message)
    }

    fun d(message: String?) {
        DEFAULT_LOGGER.d(message)
    }

    fun w(tag: String?, message: String?) {
        getLogger(tag).d(message)
    }

    fun w(message: String?) {
        DEFAULT_LOGGER.w(message)
    }

    fun w(tag: String?, message: String?, throwable: Throwable?) {
        getLogger(tag).w(message, throwable)
    }

    fun w(message: String?, throwable: Throwable?) {
        DEFAULT_LOGGER.w(message, throwable)
    }

    fun e(tag: String?, message: String?, throwable: Throwable?) {
        getLogger(tag).e(message, throwable)
    }

    fun e(message: String?, throwable: Throwable?) {
        DEFAULT_LOGGER.e(message, throwable)
    }

    fun tag(tag: String?): Logger? {
        val logger: Logger = sLoggerFactory.provideLogger("")
        logger.setTag(tag)
        return logger
    }

    interface LoggerFactory {

        fun provideLogger(tag: String?): Logger
    }

    class Logger {

        private var tag: String? = null

        private val mLocal = ThreadLocal<String>()

        constructor(tag: String?) {
            this.tag = tag
        }

        fun setTag(localTag: String?) {
            mLocal.set(localTag)
        }

        private fun fetchTAG(): String? {
            val local = mLocal.get()
            if (local != null) {
                mLocal.remove()
                return local
            }
            return if (TextUtils.isEmpty(tag)) LOGGER_TAG_DEFAULT else tag
        }

        fun i(message: String?) {
            if (isLoggable) {
                Log.i(fetchTAG(), format(message) ?: "")
            }
        }

        fun d(message: String?) {
            if (isLoggable) {
                Log.d(fetchTAG(), format(message) ?: "")
            }
        }

        private fun format(message: String?): String? {
            return String.format(">>> %1\$s <<<", message)
        }

        fun w(message: String?) {
            if (isLoggable) {
                Log.w(fetchTAG(), format(message) ?: "")
            }
        }

        fun w(message: String?, throwable: Throwable?) {
            if (isLoggable) {
                Log.i(fetchTAG(), format(message) ?: "", throwable)
            }
        }

        fun e(message: String?, throwable: Throwable?) {
            if (isLoggable) {
                Log.e(fetchTAG(), format(message) ?: "", throwable)
            }
        }
    }
}
