package com.lj.framemonitor.monitor;

/**
 * Description 处理监视Frame绘制结果基类
 * Created by langjian on 2017/3/27.
 * Version
 */

public abstract class AbsMonitor {
    //经验值，一般来讲，45帧不会卡顿(泡泡项目中，使用35帧更合理)
    protected static final long DEFAULT_FPS_THRESHOLD = 35;
    //经验值，小于5帧时，则任务不是动画
    protected static final long DEFAULT_MIN_FPS_THRESHOLD = 5;
    //处理的非帧message数量
    protected int mTotalNotFrameCount;
    //帧总数
    protected int mTotalFrameCount;
    //开始监测的时间戳
    protected long mMonitorStartTime;
    protected long mCustomMonitorTimeThreshold;
    protected long mTotalMonitorTime;
    protected int mFps;
    protected StringBuilder mMessageStringBuilder;
    protected boolean mStarted = false;

    public AbsMonitor(int duration) {
        mCustomMonitorTimeThreshold = duration;
        if(mMessageStringBuilder == null){
            mMessageStringBuilder = new StringBuilder();
        }else{
            mMessageStringBuilder.setLength(0);//清空StringBuffer
        }
    }

    protected void start(){
        if(!mStarted){
            mStarted = true;
        }
        mMonitorStartTime = System.currentTimeMillis();
    }

    protected void stop(){
        if(mStarted){
            clear();
            mStarted = false;
        }
    }

    protected void clear() {
        mTotalFrameCount = 0;
        mTotalNotFrameCount = 0;
        mMonitorStartTime = 0;
        mTotalMonitorTime = 0;
        mFps = 0;
    }

    protected void appendFrameCount(){
        if(mStarted){
            mTotalFrameCount++;
        }
    }

    protected boolean checkIfTimeout(){
        if(!mStarted){
            return false;
        }
        //监控总时长
        mTotalMonitorTime = (System.currentTimeMillis() - mMonitorStartTime);
        //若检测时间超过预设值，结束计时，分类预警处理，并重新开始检测
        if(mTotalMonitorTime > mCustomMonitorTimeThreshold){
            return true;
        }
        return false;
    }

    protected void restart() {
        clear();
        start();
    }

    protected abstract void process();

    public boolean isStarted() {
        return mStarted;
    }

    /**
     * looper处理message时，控制监控器是否采集数据
     */
    public void check() {
        if(checkIfTimeout()){
            process();
            restart();
        }
    }
}
