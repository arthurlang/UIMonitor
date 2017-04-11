package com.lj.framemonitor.monitor;

import android.os.Debug;
import android.util.Log;


/**
 * 监控主线程耗时消息
 * Created by sunxuewei on 2017/3/15.参考学委同学
 */

public class AbnormalMessageMonitor {
    private static final String TAG = "MainThreadMonitor";
    /** 最高优先级阈值，有可能引发ANR */
    private static final int THRESHOLD_0 = 5000;
    /** 次高优先级阈值，有可能引发卡顿 */
    private static final int THRESHOLD_1 = 1000;
    /** 第三优先级阈值，有可能引发卡顿 */
    private static final int THRESHOLD_2 = 200;

    /** 记录消息开始处理的时间 */
    private long mDispatchTime;
    /** 自定义的阈值 */
    private int mCustomerThreshold;
    /** 消息开始处理时的信息 */
    private String mDispatchString;

    void setCustomerThreshold (int threshold) {
        mCustomerThreshold = threshold;
    }

    void startMessage(String x) {
        mDispatchTime = System.currentTimeMillis();
        mDispatchString = x;
    }

    void finishMessage(String x) {
        if (Debug.isDebuggerConnected()) {
            return;
        }

        long end = System.currentTimeMillis();
        int duration = (int) (end - mDispatchTime);

        if (duration > THRESHOLD_0) {
            Log.e(TAG, "--------long time block druation > " + THRESHOLD_0);
        }
        else if (duration > THRESHOLD_1) {
            Log.e(TAG, "--------long time block runnable druation > " + THRESHOLD_1);
        }
        else if (duration > THRESHOLD_2) {
            Log.e(TAG, "--------long time block runnable druation > " + THRESHOLD_2);
        }
        else if (mCustomerThreshold != 0 && duration > mCustomerThreshold) {
            Log.d(TAG, "Begin time:" + mDispatchTime + " msg:" + mDispatchString);
            Log.d(TAG, "End time:" + end + " msg:" + x);
            Log.d(TAG, "Time spent:" + (end - mDispatchTime) + " msg:" + x);
        }
    }

}
