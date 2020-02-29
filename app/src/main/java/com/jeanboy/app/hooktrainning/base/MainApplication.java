package com.jeanboy.app.hooktrainning.base;

import android.app.Application;
import android.content.res.AssetManager;
import android.content.res.Resources;

import com.jeanboy.app.hooktrainning.hook.HookAMS;
import com.jeanboy.app.hooktrainning.hook.HookToPlugin;

/**
 * Created by jeanboy on 2020/02/28 9:07 PM.
 */
public class MainApplication extends Application {

    private AssetManager assetManager;
    private Resources resources;

    @Override
    public void onCreate() {
        super.onCreate();
//        HookAMS.hookStartActivity(this);
//        HookAMS.hookActivityThread();

        HookToPlugin.hookDex(this);
        assetManager = HookToPlugin.hookAssetManager();
        resources = HookToPlugin.hookResources(this, assetManager);
    }

    @Override
    public AssetManager getAssets() {
        return assetManager == null ? super.getAssets() : assetManager;
    }

    @Override
    public Resources getResources() {
        return resources == null ? super.getResources() : resources;
    }

}
