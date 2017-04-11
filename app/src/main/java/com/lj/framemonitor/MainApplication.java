package com.lj.framemonitor;


import android.app.Application;

import com.lj.framemonitor.monitor.UIThreadMonitor;

/**
 * Description
 * Created by langjian on 2017/3/24.
 * Version
 */

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UIThreadMonitor.openMonitor();
    }
}
