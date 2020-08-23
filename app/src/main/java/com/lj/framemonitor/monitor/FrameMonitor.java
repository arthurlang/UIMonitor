package com.lj.framemonitor.monitor;

import android.os.Debug;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;

import static com.lj.framemonitor.util.Constant.*;

/**
 * 对frame进行监控，用于监控动画可能丢帧的情况，以及没有动画时疑似不正常的doFrame
 */

public class FrameMonitor {
    private static final String TAG = "FrameMonitor";
    private int mUIThreadHeartBeatMaxIntervalThreshold = DEFAULT_UI_THREAD_HEARTBEAT_INTERVAL_THRESHOLD_MILLIS;
    private long mStartMsgTime;//looper 处理下一条message的开始时间
    private long mLastMsgEndTime = Long.MAX_VALUE;//looper 上一次处理message结束的时间
    private boolean mDump;//是否放弃帧数据统计
    private ExplicitFrameMonitor mMonitorAnimShort300;//监视300ms左右的短动画，前提是300ms连续动画
    private ExplicitFrameMonitor mMonitorAnimLong1000;//监视1s左右的长动画，前提是1s连续动画
    private ImplicitFrameMonitor mMonitorStaticImplicit;//没有动画时，监视隐藏动画绘制

    public FrameMonitor(){
        if(mMonitorAnimShort300 == null){
            mMonitorAnimShort300 = new ExplicitFrameMonitor(MONITOR_TIME_MILLIS_ANIM_SHORT);
        }
        if(mMonitorAnimLong1000 == null){
            mMonitorAnimLong1000 = new ExplicitFrameMonitor(MONITOR_TIME_MILLIS_ANIM_LONG);
        }
        if(mMonitorStaticImplicit == null){
            mMonitorStaticImplicit = new ImplicitFrameMonitor(MONITOR_TIME_MILLIS_ANIM_IMPLICIT);
        }
    }
    void startMessage(String x) {
        Log.d(TAG,x);
        if(Debug.isDebuggerConnected()){
            return;
        }
        if(mMonitorAnimShort300 == null){
            mMonitorAnimShort300 = new ExplicitFrameMonitor(MONITOR_TIME_MILLIS_ANIM_SHORT);
        }
        if(mMonitorAnimLong1000 == null){
            mMonitorAnimLong1000 = new ExplicitFrameMonitor(MONITOR_TIME_MILLIS_ANIM_LONG);
        }
        if(mMonitorStaticImplicit == null){
            mMonitorStaticImplicit = new ImplicitFrameMonitor(MONITOR_TIME_MILLIS_ANIM_IMPLICIT);
        }

        //如果是属于Frame，开始监测
        if(isDrawFrame(x)){
            if(isDumped()){
                setDump(false);
                mMonitorAnimShort300.start();
                mMonitorAnimLong1000.start();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {//一般滑动，3s之后动画停止，打开异常监控器
                        mMonitorStaticImplicit.start();
                    }
                },3000);

            }
        }
        //记录一条message的处理开始时间
        mStartMsgTime = SystemClock.elapsedRealtime();
        //如果主线程looper处理两条Message间隔（上一条结束和下一条message开始之间的）时间大于阈值，可能是动画即将结束
        //关闭监视器，动画监控全局状态设置为dump状态，同时打开疑似隐藏动画监视器，检测5s之内，是否有正在隐藏的动画。
        //等待下一次doframe绘制，重新启动动画监视器。如果在连续动画过程中，300ms或者1s时间fps小于45，发出卡顿警报
        long messageGap = mStartMsgTime - mLastMsgEndTime;
        if(messageGap > mUIThreadHeartBeatMaxIntervalThreshold){
            if (!isDumped()){//未被丢弃
                dumpStatistics();
            }
        }
    }

    private boolean isDumped() {
        return mDump;
    }

    private void setDump(boolean dump) {
        mDump = dump;
    }

    private void dumpStatistics() {
        mMonitorAnimShort300.stop();
        mMonitorAnimLong1000.stop();
        setDump(true);
    }

    private boolean isDrawFrame(String msgInfo) {
        String callback = "android.view.Choreographer$FrameDisplayEventReceiver";
        if(!TextUtils.isEmpty(msgInfo) && msgInfo.contains(callback)){
            return true;
        }
        return false;
    }

    void finishMessage(String x) {
        if(Debug.isDebuggerConnected()){
            return;
        }

        if(!isDumped()){
            if(isDrawFrame(x)){
                //绘制总帧数
                mMonitorAnimShort300.appendFrameCount();
                mMonitorAnimLong1000.appendFrameCount();
            }

            //检查监控时间是否超过预设值
            mMonitorAnimShort300.check();
            mMonitorAnimLong1000.check();
        }

        if(isDrawFrame(x)){
            //绘制Frame总数
            mMonitorStaticImplicit.appendFrameCount();
        }else{
            //绘制非Frame总数
            mMonitorStaticImplicit.appendNotFrameCount();
        }
        mMonitorStaticImplicit.check();
        //记录一条message处理结束的时间戳
        mLastMsgEndTime = SystemClock.elapsedRealtime();
    }

    public void setUIThreadHeartBeatMaxIntervalThreshold(int UIThreadHeartBeatMaxIntervalThreshold) {
        mUIThreadHeartBeatMaxIntervalThreshold = UIThreadHeartBeatMaxIntervalThreshold;
    }
}
