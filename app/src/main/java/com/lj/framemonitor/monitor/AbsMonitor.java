package com.lj.framemonitor.monitor;

/**
 * Description 处理监视Frame绘制结果基类
 * Created by langjian on 2017/3/27.
 * Version
 */

public abstract class AbsMonitor {
    protected static final long DEFAULT_FPS_THRESHOLD = 35;//经验值，45帧不会卡顿(不精确，因此使用35帧作为是否卡顿的标记)
    protected static final long DEFAULT_MIN_FPS_THRESHOLD = 5;//经验值，小于5帧时，则任务不是动画
    protected int mTotalMessageCount;//处理的非帧message数量
    protected int mTotalFrameCount;//帧总数
    protected long mMonitorStartTime;//开始监测的时间戳
    protected long mCustomMonitorTimeThreshold;
    protected int mFps;
    protected StringBuilder mMessageStringBuilder;
    protected long mTotalMonitorTime;
    protected boolean mStarted = false;

    public AbsMonitor(int duration) {
        mCustomMonitorTimeThreshold = duration;
    }

    protected void start(){
        if(!mStarted){
            mStarted = true;
        }
        mMonitorStartTime = System.currentTimeMillis();
        if(mMessageStringBuilder == null){
            mMessageStringBuilder = new StringBuilder();
        }else{
            mMessageStringBuilder.setLength(0);//清空StringBuffer
        }
    }

    protected void stop(){
        if(mStarted){
            clear();
            mStarted = false;
        }
    }

    private void clear() {
        mTotalFrameCount = 0;
        mTotalMessageCount = 0;
        mMonitorStartTime = 0;
        mTotalMonitorTime = 0;
        mFps = 0;
    }

    protected void appendFrameCount(){
        if(mStarted){
            mTotalFrameCount++;
        }
    }

    protected void checkIfStop(){
        if(!mStarted){
            return;
        }
        //计算帧率
        mTotalMonitorTime = (System.currentTimeMillis() - mMonitorStartTime);

        //若检测时间超过预设值，结束计时，分类预警处理，并重新开始检测
        if(mTotalMonitorTime > mCustomMonitorTimeThreshold){
            mFps = (int) (mTotalFrameCount / (mTotalMonitorTime / (1000*1.0f)));
            if(mFps > 60){
                mFps = 60;
            }
            process();
            restart();
        }
    }

    private void restart() {
        clear();
        start();
    }

    protected abstract void process();
}
