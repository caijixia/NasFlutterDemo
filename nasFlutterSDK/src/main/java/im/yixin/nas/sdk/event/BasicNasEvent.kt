package im.yixin.nas.sdk.event

import im.yixin.nas.sdk.NasBundle

/**
 * Created by jixia.cai on 2021/2/24 7:58 PM
 */
abstract class BasicNasEvent {

    abstract fun toBundle(): NasBundle
}