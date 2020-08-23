package com.lj.framemonitor.util

import kotlin.math.roundToInt

const val A= 1
public object Config {
    //经验值，一般来讲，45帧不会卡顿(泡泡项目中，使用35帧更合理)
    const val DEFAULT_FPS_THRESHOLD: Int = 35

    //经验值，小于5帧时，则任务不是动画
    const val DEFAULT_MIN_FPS_THRESHOLD: Long = 5

    val IS_BLOCK_MSG_THRESHOLD = (1000 / DEFAULT_FPS_THRESHOLD).toDouble().roundToInt()

}