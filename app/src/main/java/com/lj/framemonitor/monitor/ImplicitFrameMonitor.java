package com.lj.framemonitor.monitor;

import android.util.Log;

/**
 * Description 前端页面没有动画时,
 * 1)后端doFrame仍然在绘制的不正常现象
 * 2)以及主线程循环处理handler非正常现象
 * Created by langjian on 2017/3/27.
 * Version
 */

public class ImplicitFrameMonitor extends AbsMonitor{
    private static final String TAG = ImplicitFrameMonitor.class.getSimpleName();
    private static final int MINIMUM_COUNT_THRESHOLD = 5;

    public ImplicitFrameMonitor(int duration) {
        super(duration);
    }

    @Override
    protected void process() {
        if(mMessageStringBuilder == null){
            return;
        }
        mMessageStringBuilder.setLength(0);
        mMessageStringBuilder.append("--------").append(" totalMonitorTime:").append(mTotalMonitorTime);
        if(mTotalNotFrameCount >= MINIMUM_COUNT_THRESHOLD){//异常message超过5条
            mMessageStringBuilder.append(" messageCount=")
                    .append(mTotalNotFrameCount).append(" handler is running");
            Log.e(TAG, mMessageStringBuilder.toString());
        }
        mMessageStringBuilder.setLength(0);
        if(mTotalFrameCount >= MINIMUM_COUNT_THRESHOLD){//异常frame超过5条
            mMessageStringBuilder.append("--------").append(" totalMonitorTime:").append(mTotalMonitorTime).append(" frameCount:")
                    .append(mTotalFrameCount)
                    .append("frame is doing");
            Log.e(TAG, mMessageStringBuilder.toString());
        }

        //附上stackTrace(),找到调用的activity
        StackTraceElement[] trace =  Thread.currentThread().getStackTrace();
        for (StackTraceElement traceElement : trace)
            mMessageStringBuilder.append("\tat " + traceElement);
    }

    public void appendNotFrameCount() {
        if(mStarted){
            mTotalNotFrameCount++;
        }
    }
}
