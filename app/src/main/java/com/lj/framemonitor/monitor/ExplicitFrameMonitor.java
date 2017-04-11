package com.lj.framemonitor.monitor;


import static com.lj.framemonitor.monitor.FrameMonitor.MONITOR_TIME_MILLIS_ANIM_LONG;
import static com.lj.framemonitor.monitor.FrameMonitor.MONITOR_TIME_MILLIS_ANIM_SHORT;

import android.util.Log;

/**
 * Description 前端页面绘制正在绘制的动画是否卡顿
 * Version
 */

public class ExplicitFrameMonitor extends AbsMonitor {
    private static final String TAG = ExplicitFrameMonitor.class.getSimpleName();

    public ExplicitFrameMonitor(int duration) {
        super(duration);
    }

    @Override
    protected void process() {

        mMessageStringBuilder.setLength(0);
        mMessageStringBuilder.append(" fps=")
                .append(mFps)
                .append(" frameCount:")
                .append(mTotalFrameCount)
                .append(" totalMonitorTime:")
                .append(mTotalMonitorTime);

        if(mCustomMonitorTimeThreshold == MONITOR_TIME_MILLIS_ANIM_LONG){//1000秒长动画
            if(isBlock()){
                Log.e(TAG, "--------block (<"+DEFAULT_FPS_THRESHOLD+")" + mMessageStringBuilder.toString() + "  long animation");
            }
        }else if(mCustomMonitorTimeThreshold == MONITOR_TIME_MILLIS_ANIM_SHORT){//300秒短动画
            if(isBlock()){
                Log.e(TAG, "--------block (<"+DEFAULT_FPS_THRESHOLD+")" + mMessageStringBuilder.toString() + "   short animation");
            }
        }
    }

    private boolean isBlock() {
        boolean block = (mFps >= DEFAULT_MIN_FPS_THRESHOLD) && (mFps <= DEFAULT_FPS_THRESHOLD);
        return block;
    }
}
