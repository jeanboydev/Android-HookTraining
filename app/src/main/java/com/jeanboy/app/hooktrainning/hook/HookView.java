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
            @SuppressLint("DiscouragedPrivateApi")
            Method getListenerInfo = View.class.getDeclaredMethod("getListenerInfo");
            getListenerInfo.setAccessible(true);
            // 调用 view 的 getListenerInfo() 获取到 ListenerInfo
            Object listenerInfo = getListenerInfo.invoke(view);

            @SuppressLint("PrivateApi")
            Class<?> listenerInfoClass = Class.forName("android.view.View$ListenerInfo");
            Field mOnClickListener = listenerInfoClass.getDeclaredField("mOnClickListener");
            mOnClickListener.setAccessible(true);
            // 获取 mOnClickListener 属性的值
            View.OnClickListener originOnClickListener = (View.OnClickListener) mOnClickListener.get(listenerInfo);

            HookedOnClickListener hookedOnClickListener = new HookedOnClickListener(originOnClickListener);
            // 为 mOnClickListener 属性重新赋值
            mOnClickListener.set(listenerInfo, hookedOnClickListener);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
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
