package com.lj.framemonitor.monitor;

import android.util.Log;

import static com.lj.framemonitor.util.Config.DEFAULT_FPS_THRESHOLD;
import static com.lj.framemonitor.util.Config.DEFAULT_MIN_FPS_THRESHOLD;
import static com.lj.framemonitor.util.Constant.*;

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

        if(mMessageStringBuilder == null){
            mMessageStringBuilder = new StringBuilder();
        }else{
            mMessageStringBuilder.setLength(0);//清空StringBuffer
        }
        mMessageStringBuilder.append(" fps=")
                .append(calculateFps())
                .append(" frameCount:")
                .append(mTotalFrameCount)
                .append(" totalMonitorTime:")
                .append(mTotalMonitorTime);

        if(mCustomMonitorTimeThreshold == MONITOR_TIME_MILLIS_ANIM_LONG){//1000秒长动画
            if(isBlock()){
                Log.e(TAG, "--------block (<"+ DEFAULT_FPS_THRESHOLD+")" + mMessageStringBuilder.toString() + "  long animation");
            }
        }else if(mCustomMonitorTimeThreshold == MONITOR_TIME_MILLIS_ANIM_SHORT){//300秒短动画
            if(isBlock()){
                Log.e(TAG, "--------block (<"+DEFAULT_FPS_THRESHOLD+")" + mMessageStringBuilder.toString() + "   short animation");
            }
        }
    }

    private int calculateFps() {
        mFps = (int) (mTotalFrameCount / (mTotalMonitorTime / (1000*1.0f)));
        if(mFps > 60){//修复扩大过程中的误差
            mFps = 60;
        }
        return mFps;
    }

    private boolean isBlock() {
        boolean block = (mFps >= DEFAULT_MIN_FPS_THRESHOLD) && (mFps <= DEFAULT_FPS_THRESHOLD);
        return block;
    }
}
