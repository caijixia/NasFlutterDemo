package im.yixin.nas.sdk.util

import kotlin.random.Random

/**
 * Created by jixia.cai on 2021/2/24 4:37 PM
 */
object RandomUtil {

    private const val numeric = "0123456789"

    private const val alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"

    private const val character = "$numeric$alphabet"

    fun nextString(num: Int? = 10): String {
        val builder = StringBuilder()
        for (i in 0 until (num ?: 10)) {
            val number = Random.nextInt(character.length)
            val char = character[number]
            builder.append(char)
        }
        return builder.toString()
    }
}
