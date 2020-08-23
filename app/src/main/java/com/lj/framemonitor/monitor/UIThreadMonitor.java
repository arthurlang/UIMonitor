package com.lj.framemonitor.monitor;

import android.os.Looper;
import android.util.Log;
import android.util.Printer;

import com.lj.framemonitor.util.MyLog;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

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
    public static boolean sMultiThread;
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
        Looper looper = null;
        if(sMultiThread) {
            ThreadGroup group = Thread.currentThread().getThreadGroup();
            ThreadGroup topGroup = group;
            // 遍历线程组树，获取根线程组
            while (group != null) {
                topGroup = group;
                group = group.getParent();
            }
            // 激活的线程数再加一倍，防止枚举时有可能刚好有动态线程生成
            int slackSize = topGroup.activeCount() * 2;
            Thread[] slackThreads = new Thread[slackSize];
            // 获取根线程组下的所有线程，返回的actualSize便是最终的线程数
            int actualSize = topGroup.enumerate(slackThreads);
            Thread[] atualThreads = new Thread[actualSize];
            // 复制slackThreads中有效的值到atualThreads
            System.arraycopy(slackThreads, 0, atualThreads, 0, actualSize);
            System.out.println("Threads size is " + atualThreads.length);
            for (Thread thread : atualThreads) {
                System.out.println("Thread name : " + thread.getName());
                try {
                    Class classLocal = Class.forName("android.os.Looper");
                    Field field = classLocal.getDeclaredField("sThreadLocal");
                    field.setAccessible(true);
                    ThreadLocal threadLocal = (ThreadLocal) field.get(classLocal);
                    Field fieldLocalMap = thread.getClass().getDeclaredField("threadLocals");
                    fieldLocalMap.setAccessible(true);
                    if(fieldLocalMap.get(thread) == null)continue;
                    //通过反射获取私有内部类对象
                    Class threadLocalClass = ThreadLocal.class;
                    Class innerClazz[] = threadLocalClass.getDeclaredClasses();
                    for (Class c : innerClazz) {
                        if (c.getSimpleName().contains("ThreadLocalMap")) {
                            Method method =null;
                            try {
                                //todo 为啥访问不到ThreadLocalMap任何属性(getDeclaredMethods())？？？？？导致此处method == null
                                method = c.getDeclaredMethod("getEntry", threadLocalClass);
                            } catch (Exception e) {
                                System.out.println("-------- " + e.getCause().getMessage());
                            }
                            if(method != null) {
                                method.setAccessible(true);
                                Object entryInstance = method.invoke(fieldLocalMap.get(thread),threadLocal);
                                if (entryInstance != null) {
                                    Class c2 = Class.forName("java.lang.ThreadLocal$ThreadLocalMap$Entry");
                                    Field field1 = c2.getField("value");
                                    looper = (Looper)field1.get(entryInstance);
                                    System.out.println("looper is " + looper.toString());
                                }
                            }
                        }
                    }
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                }catch (InvocationTargetException e) {
                    e.printStackTrace();
                } catch (NoSuchFieldException e) {
                    e.printStackTrace();
                }
            }
        } else {
           looper =  Looper.getMainLooper();
        }
        if(looper != null) {
            looper.setMessageLogging(new Printer() {
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
