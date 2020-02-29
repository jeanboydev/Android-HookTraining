package com.jeanboy.app.hooktrainning.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jeanboy on 2020/02/28 11:00 AM.
 */
public class HookInstrumentation extends Instrumentation {
    private final Instrumentation origin;

    public HookInstrumentation(Instrumentation origin) {
        this.origin = origin;
    }

    public void hook() {
        try {
            // 反射获取到 ActivityThread 的 Class 对象
            @SuppressLint("PrivateApi")
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            // 获取 ActivityThread.currentActivityThread() 方法
            @SuppressLint("DiscouragedPrivateApi")
            Method activityThread = atClass.getDeclaredMethod("currentActivityThread");
            // 设置可访问
            activityThread.setAccessible(true);
            // 执行方法，获取到返回值
            Object currentThread = activityThread.invoke(null);
            // 获取到 mInstrumentation 成员变量
            Field mInstrumentation = atClass.getDeclaredField("mInstrumentation");
            // 设置可访问
            mInstrumentation.setAccessible(true);
            // 获取到成员变量的值
            Instrumentation instrumentationInfo = (Instrumentation) mInstrumentation.get(currentThread);
            // 创建代理对象
            HookInstrumentation hookInstrumentation = new HookInstrumentation(instrumentationInfo);
            // 为成员变量重新赋值
            mInstrumentation.set(currentThread, hookInstrumentation);
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }



}
