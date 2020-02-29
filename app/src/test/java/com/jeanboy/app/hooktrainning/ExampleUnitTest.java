package com.jeanboy.app.hooktrainning;

import com.jeanboy.app.hooktrainning.proxy.dynamic.ProxyHandler;
import com.jeanboy.app.hooktrainning.proxy.normal.UserService;
import com.jeanboy.app.hooktrainning.proxy.normal.UserServiceImpl;
import com.jeanboy.app.hooktrainning.proxy.normal.UserServiceProxy;
import com.jeanboy.app.hooktrainning.reflect.UserModel;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void proxyStaticTest() {
        UserService userService = new UserServiceImpl();
        UserServiceProxy userServiceProxy = new UserServiceProxy(userService);
        userServiceProxy.select();
    }

    @Test
    public void proxyDynamicTest() {
        UserService userService = new UserServiceImpl();
        ClassLoader classLoader = userService.getClass().getClassLoader();
        Class<?>[] interfaces = userService.getClass().getInterfaces();
        ProxyHandler proxyHandler = new ProxyHandler(userService);
        UserService proxy = (UserService) Proxy.newProxyInstance(classLoader, interfaces, proxyHandler);
        proxy.select();
    }

    @Test
    public void reflectTest() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        // 获取 Class 对象
        Class<?> userModelClass = Class.forName("com.jeanboy.app.hooktrainning.reflect.UserModel");

        Class<Integer> intClass = int.class;
        Class<Integer> type = Integer.TYPE;

        StringBuilder stringBuilder = new StringBuilder("123");
        Class<? extends StringBuilder> aClass = stringBuilder.getClass();

        // 判断是否为某个类实例
        userModelClass.isInstance(UserModel.class);

        // 创建实例
        Object instance = userModelClass.newInstance();
        Constructor<?> constructor = userModelClass.getConstructor();
        Object o = constructor.newInstance();

        // 获取方法
        // 返回类或接口声明的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法
        userModelClass.getDeclaredMethods();
        // 返回某个类的所有公用（public）方法，包括其继承类的公用方法
        userModelClass.getMethods();
        // 返回一个特定的方法，其中第一个参数为方法名称，后面的参数为方法的参数对应Class的对象
        Method setUsername = userModelClass.getMethod("setUsername", String.class);

        // 获取成员变量
        // 访问公有的成员变量
        userModelClass.getField("username");
        // 所有已声明的成员变量，但不能得到其父类的成员变量
        userModelClass.getDeclaredField("username");
        // 返回类或接口声明的所有成员变量，包括公共、保护、默认（包）访问和私有成员变量，但不包括继承的成员变量
        userModelClass.getDeclaredFields();
        // 返回某个类的所有公有（public）成员变量，包括其继承类的公有成员变量
        userModelClass.getFields();

        // 调用方法
        setUsername.invoke(instance, "aa");
    }


    @Test
    public void reflectTest2() throws ClassNotFoundException, InstantiationException, IllegalAccessException, NoSuchMethodException, InvocationTargetException, NoSuchFieldException {
        Class<?> userClass = Class.forName("com.jeanboy.app.hooktrainning.reflect.UserModel");
        Object newInstance = userClass.newInstance();

        Method setUsername = userClass.getMethod("setUsername", String.class);
        setUsername.invoke(newInstance, "test");

        Field age = userClass.getDeclaredField("age");
        age.setAccessible(true);
        age.setInt(newInstance, 18);

        Method update = userClass.getDeclaredMethod("update");
        update.setAccessible(true);
        update.invoke(newInstance);

        Method toString = userClass.getMethod("toString");
        Object result = toString.invoke(newInstance);
        System.out.println(result);
    }
}