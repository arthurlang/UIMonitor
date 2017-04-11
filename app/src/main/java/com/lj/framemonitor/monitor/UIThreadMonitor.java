package com.lj.framemonitor.monitor;

import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.lj.framemonitor.test.MyLog;

/**
 * Description 主线程页面性能监控器
 * 1、监控主线程耗时消息
 * 2、监控连续动画过程中是否流畅
 * 3、监控静止界面下是否存在隐藏动画或者handler在循环调用
 * Created by langjian on 2017/3/24.
 * Version
 */

public class UIThreadMonitor {
    private static final String TAG = "MainThreadMonitor";
    /** 消息开始处理标记 */
    private static boolean sPrintingStarted;
    /** 监控处理时间过长的消息 */
    private static AbnormalMessageMonitor sMessageMonitor = new AbnormalMessageMonitor();
    /** 监控frame是否正常 */
    private static FrameMonitor sFrameMonitor = new FrameMonitor();

    /**
     * 调用此方法开启主线程监控
     */
    public static void openMonitor() {
        if (!MyLog.isDebug()) {
            return;
        }
        sPrintingStarted = false;
        Looper.getMainLooper().setMessageLogging(new Printer() {
            @Override
            public void println(String x) {
                if (!sPrintingStarted) {
                    sMessageMonitor.startMessage(x);
                    sFrameMonitor.startMessage(x);
                    sPrintingStarted = true;
                } else {
                    sPrintingStarted = false;
                    sMessageMonitor.finishMessage(x);
                    sFrameMonitor.finishMessage(x);
                }
            }
        });
    }

    /**
     * 关闭监控
     */
    public static void closeMonitor() {
        if (!MyLog.isDebug()) {
            return;
        }

        Looper.getMainLooper().setMessageLogging(null);
    }

    /**
     * 用于设置消息监控的自定义阈值
     * @param threshold
     */
    public static void setCustomerThreshold(int threshold) {
        sMessageMonitor.setCustomerThreshold(threshold);
    }

    public static void log(String message) {
        Log.d(TAG, message);
    }

}
