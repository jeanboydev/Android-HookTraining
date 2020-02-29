package com.jeanboy.app.hooktrainning.hook;

import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

/**
 * Created by jeanboy on 2020/02/29 9:53 AM.
 */
public class HookToPlugin {

    public static void hookDex(Context context) {
        PathClassLoader pathClassLoader = (PathClassLoader) context.getClassLoader();
        try {
            Class<?> bdClass = Class.forName("dalvik.system.BaseDexClassLoader");
            Field pathListField = bdClass.getDeclaredField("pathList");
            pathListField.setAccessible(true);
            Object dexPathList = pathListField.get(pathClassLoader);

            Field dexElementsField = dexPathList.getClass().getDeclaredField("dexElements");
            dexElementsField.setAccessible(true);
            Object appDexElements = dexElementsField.get(dexPathList);

            File pluginFile = new File(Environment.getExternalStorageDirectory() + File.separator + "plugin-debug.apk");
            if (!pluginFile.exists()) {
                Log.e(HookToPlugin.class.getSimpleName(), "插件包不存在");
                return;
            }
            String pluginPath = pluginFile.getAbsolutePath();
            File pluginDir = context.getDir("pluginDir", Context.MODE_PRIVATE);
            DexClassLoader dexClassLoader = new DexClassLoader(pluginPath, pluginDir.getAbsolutePath(), null, pathClassLoader);
            Object pluginDexPathList = pathListField.get(dexClassLoader);
            Field pluginDexElementsField = pluginDexPathList.getClass().getDeclaredField("dexElements");
            pluginDexElementsField.setAccessible(true);
            Object pluginDexElements = pluginDexElementsField.get(pluginDexPathList);

            int appLength = Array.getLength(appDexElements);
            int pluginLength = Array.getLength(pluginDexElements);
            int total = appLength + pluginLength;
            Object newDexElements = Array.newInstance(appDexElements.getClass().getComponentType(), total);
            for (int i = 0; i < total; i++) {
                if (i < appLength) {
                    Array.set(newDexElements, i, Array.get(appDexElements, i));
                } else {
                    Array.set(newDexElements, i, Array.get(pluginDexElements, i - appLength));
                }
            }
            dexElementsField.set(dexPathList, newDexElements);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }


    public static AssetManager hookAssetManager() {
        try {
            AssetManager assetManager = AssetManager.class.newInstance();

            File pluginFile = new File(Environment.getExternalStorageDirectory() + File.separator + "plugin-debug.apk");
            if (!pluginFile.exists()) {
                Log.e(HookToPlugin.class.getSimpleName(), "插件包不存在");
                return null;
            }
            String pluginPath = pluginFile.getAbsolutePath();

            Log.e(HookToPlugin.class.getSimpleName(), "----pluginPath--" + pluginPath);

            Method addAssetPath = assetManager.getClass().getDeclaredMethod("addAssetPath", String.class);
            addAssetPath.setAccessible(true);
            addAssetPath.invoke(assetManager, pluginPath);
            return assetManager;
        } catch (IllegalAccessException | InstantiationException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Resources hookResources(Context context, AssetManager assetManager) {
        Resources r = context.getResources();
        Resources resources = new Resources(assetManager, r.getDisplayMetrics(), r.getConfiguration());
        return resources;
    }
}
