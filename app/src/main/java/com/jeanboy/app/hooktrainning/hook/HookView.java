package com.jeanboy.app.hooktrainning.hook;

import android.annotation.SuppressLint;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by jeanboy on 2020/02/27 8:33 PM.
 */
public class HookView {
    public static void hookOnClickListener(View view) {
        try {
            // 通过反射获取到 getListenerInfo() 方法
            @SuppressLint("DiscouragedPrivateApi")
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            // 设置访问权限
            getListenerInfo.setAccessible(true);
            // 调用 view 的 getListenerInfo() 获取到 ListenerInfo
            Object listenerInfo = getListenerInfo.invoke(view);

            // 通过反射获取到 ListenerInfo 的 Class 对象
            @SuppressLint("PrivateApi")
            Class<?> listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
            // 获取到 mOnClickListener 成员变量
            Field mOnClickListener = listenerInfoClass.getDeclaredField("mOnClickListener");
            // 设置访问权限
            mOnClickListener.setAccessible(true);
            // 获取 mOnClickListener 属性的值
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);

            // 创建 OnClickListener 代理对象
            HookedOnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            // 为 mOnClickListener 属性重新赋值
            mOnClickListener.set(listenerInfo, hookedOnClickListener);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    static class HookedOnClickListener implements View.OnClickListener {

        private final View.OnClickListener origin;

        HookedOnClickListener(View.OnClickListener origin) {
            this.origin = origin;
        }

        @Override
        public void onClick(View v) {
            Log.e("HookedOnClickListener", "onClick");
            if (origin != null) {
                origin.onClick(v);
            }
        }
    }
}
