package com.jeanboy.app.hooktrainning.proxy.normal;

/**
 * Created by jeanboy on 2020/02/27 10:54 AM.
 */
public class UserServiceProxy implements UserService {

    private final UserService target; // 被代理对象

    public UserServiceProxy(UserService target) {
        this.target = target;
    }

    @Override
    public void select() {
        before();
        target.select();
        after();
    }

    @Override
    public void update() {
        before();
        target.update();
        after();
    }

    private void before(){
        System.out.println("静态代理-----方法执行前");
    }

    private void after(){
        System.out.println("静态代理-----方法执行后");
    }
}
