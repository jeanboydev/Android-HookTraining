package com.jeanboy.app.hooktrainning.proxy.normal;

/**
 * Created by jeanboy on 2020/02/27 10:53 AM.
 */
public class UserServiceImpl implements UserService {
    @Override
    public void select() {
        System.out.println("查询");
    }

    @Override
    public void update() {
        System.out.println("更新");
    }
}
