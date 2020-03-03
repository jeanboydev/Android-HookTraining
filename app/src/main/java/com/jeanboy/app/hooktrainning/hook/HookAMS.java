package com.jeanboy.app.hooktrainning.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import com.jeanboy.app.hooktrainning.ProxyActivity;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;

/**
 * Created by jeanboy on 2020/02/28 7:33 PM.
 */
public class HookAMS {

    public static void hookStartActivity(final Context context) {
        try {
            // 获取到 ActivityTaskManager 的 Class 对象
            @SuppressLint("PrivateApi")
            Class<?> amClass = Class.forName("android.app.ActivityManager");
            // 获取到 IActivityTaskManagerSingleton 成员变量
            Field iActivityTaskManagerSingletonField = amClass.getDeclaredField("IActivityManagerSingleton");
            iActivityTaskManagerSingletonField.setAccessible(true);
            // 获取 IActivityTaskManagerSingleton 成员变量的值
            Object IActivityTaskManagerSingleton = iActivityTaskManagerSingletonField.get(null);

            // 获取 getService() 方法
            @SuppressLint("BlockedPrivateApi")
            Method getService = amClass.getDeclaredMethod("getService");
            getService.setAccessible(true);
            // 执行 getService() 方法
            final Object IActivityTaskManager = getService.invoke(null);

            // 获取到 IActivityTaskManager 的 Class 对象
            @SuppressLint("PrivateApi")
            Class<?> iamClass = Class.forName("android.app.IActivityManager");
            // 创建代理类 IActivityTaskManager
            Object proxyIActivityManager = Proxy.newProxyInstance(context.getClassLoader(), new Class[]{iamClass}, new InvocationHandler() {
                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    if ("startActivity".equals(method.getName())) {
                        Intent proxyIntent = new Intent(context, ProxyActivity.class);
                        // startActivity 第三个参数为 Intent
                        proxyIntent.putExtra("targetIntent", (Intent) args[2]);
                        args[2] = proxyIntent;
                    }
                    return method.invoke(IActivityTaskManager, args);
                }
            });

            // 获取到 Singleton 的 Class 对象
            @SuppressLint("PrivateApi")
            Class<?> sClass = Class.forName("android.util.Singleton");
            // 获取到 mInstance 成员变量
            Field mInstanceField = sClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            // 赋值 proxyIActivityManager 给 mInstance 成员变量
            mInstanceField.set(IActivityTaskManagerSingleton, proxyIActivityManager);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public static void hookActivityThread() {
        try {
            // 获取到 mH 对象
            @SuppressLint("PrivateApi")
            Class<?> atClass = Class.forName("android.app.ActivityThread");
            Field mHField = atClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            // 获取到 ActivityThread 对象
            @SuppressLint("DiscouragedPrivateApi")
            Method currentActivityThreadMethod = atClass.getDeclaredMethod("currentActivityThread");
            Object currentActivityThread = currentActivityThreadMethod.invoke(null);
            Object mH = mHField.get(currentActivityThread);
            // 拿到 mCallback 替换成我们自己的
            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(mH, new MyCallback());
        } catch (ClassNotFoundException | NoSuchFieldException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private static class MyCallback implements Handler.Callback {

        @Override
        public boolean handleMessage(@NonNull Message msg) {
            Object clientTransactionObj = msg.obj;

            try {
                @SuppressLint("PrivateApi")
                Class<?> laiClass = Class.forName("android.app.servertransaction.LaunchActivityItem");

                Field mActivityCallbacksField = clientTransactionObj.getClass().getDeclaredField("mActivityCallbacks");
                mActivityCallbacksField.setAccessible(true);
                List activityCallbackList = (List) mActivityCallbacksField.get(clientTransactionObj);
                if (activityCallbackList == null || activityCallbackList.size() == 0) {
                    return false;
                }
                Object mLaunchActivityItem = activityCallbackList.get(0);
                if (!laiClass.isInstance(mLaunchActivityItem)) {
                    return false;
                }
                Field mIntentField = laiClass.getDeclaredField("mIntent");
                mIntentField.setAccessible(true);
                // 获取代理的 Intent
                Intent proxyIntent = (Intent) mIntentField.get(mLaunchActivityItem);
                if (proxyIntent == null) {
                    return false;
                }
                // 获取到前面传入的 targetIntent
                Intent targetIntent = proxyIntent.getParcelableExtra("targetIntent");
                if (targetIntent != null) {
                    // 替换 Intent
                    mIntentField.set(mLaunchActivityItem, targetIntent);
                }
            } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            return false;
        }
    }
}
