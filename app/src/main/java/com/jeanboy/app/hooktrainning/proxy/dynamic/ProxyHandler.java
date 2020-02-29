package com.jeanboy.app.hooktrainning.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by jeanboy on 2020/02/27 11:05 AM.
 */
public class ProxyHandler implements InvocationHandler {

    private final Object target; // 被代理对象

    public ProxyHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before();
        // 调用 target 的 method 方法
        Object result = method.invoke(target, args);
        after();
        return result;
    }

    private void before() {
        System.out.println("动态代理-----方法执行前");
    }

    private void after() {
        System.out.println("动态代理-----方法执行后");
    }
}
