# Android-HookTraining

## 代理

### 代理模式

给某一个对象提供一个代理，并由代理对象来控制对真实对象的访问。代理模式是一种结构型设计模式。

### 静态代理

所谓静态也就是在程序运行前就已经存在代理类的字节码文件，代理类和真实主题角色的关系在运行前就确定了。

### 动态代理

动态代理的源码是在程序运行期间由JVM根据反射等机制动态的生成，所以在运行前并不存在代理类的字节码文件。

- ProxyHandler

```java
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
```

- 测试

```java
UserService userService = new UserServiceImpl();
ClassLoader classLoader = userService.getClass().getClassLoader();
Class<?>[] interfaces = userService.getClass().getInterfaces();
ProxyHandler proxyHandler = new ProxyHandler(userService);
UserService proxy = (UserService) Proxy.newProxyInstance(classLoader, interfaces, proxyHandler);
proxy.select();
```

- Proxy.newProxyInstance

```java
public static Object newProxyInstance(ClassLoader loader,
                                      Class<?>[] interfaces,
                                      InvocationHandler h)
  throws IllegalArgumentException
{
  // 要求 h 非空，否则抛出空指针异常
  Objects.requireNonNull(h);
	// 克隆一份代理目标类所实现的所有接口的 class 对象数组
  final Class<?>[] intfs = interfaces.clone();
  // 拿到代理类的 class 对象
  Class<?> cl = getProxyClass0(loader, intfs);
  
  try {
    // 拿到代理类的构造方法
    final Constructor<?> cons = cl.getConstructor(constructorParams);
    final InvocationHandler ih = h;
    // 访问修饰符
    if (!Modifier.isPublic(cl.getModifiers())) {
      cons.setAccessible(true);
    }
    // 返回构造的代理对象
    return cons.newInstance(new Object[]{h});
  } catch (IllegalAccessException|InstantiationException e) {
    throw new InternalError(e.toString(), e);
  } catch (InvocationTargetException e) {
    Throwable t = e.getCause();
    if (t instanceof RuntimeException) {
      throw (RuntimeException) t;
    } else {
      throw new InternalError(t.toString(), t);
    }
  } catch (NoSuchMethodException e) {
    throw new InternalError(e.toString(), e);
  }
}
```

## Java 反射

反射 (Reflection) 是 Java 的特征之一，它允许运行中的 Java 程序获取自身的信息，并且可以操作类或对象的内部属性。

Oracle 官方对反射的解释是：

> Reflection enables Java code to discover information about the fields, methods and constructors of loaded classes, and to use reflected fields, methods, and constructors to operate on their underlying counterparts, within security restrictions.
> The API accommodates applications that need access to either the public members of a target object (based on its runtime class) or the members declared by a given class. It also allows programs to suppress default reflective access control.

简而言之，通过反射，我们可以在运行时获得程序或程序集中每一个类型的成员和成员的信息。程序中一般的对象的类型都是在编译期就确定下来的，而 Java 反射机制可以动态地创建对象并调用其属性，这样的对象的类型在编译期是未知的。所以我们可以通过反射机制直接创建对象，即使这个对象的类型在编译期是未知的。

反射的核心是 JVM 在运行时才动态加载类或调用方法/访问属性，它不需要事先（写代码的时候或编译期）知道运行对象是谁。

### 获取 Class 对象

```java
Class<?> userModelClass = Class.forName("UserModel");

Class<Integer> intClass = int.class;
Class<Integer> type = Integer.TYPE;

StringBuilder stringBuilder=new StringBuilder("123");
Class<? extends StringBuilder> aClass = stringBuilder.getClass();
```

### 创建实例

```java
Object instance = userModelClass.newInstance();
Constructor<?> constructor = userModelClass.getConstructor();
Object o = constructor.newInstance();
```

### 获取方法

```java
/ 返回类或接口声明的所有方法，包括公共、保护、默认（包）访问和私有方法，但不包括继承的方法
userModelClass.getDeclaredMethods();
// 返回某个类的所有公用（public）方法，包括其继承类的公用方法
userModelClass.getMethods();
// 返回一个特定的方法，其中第一个参数为方法名称，后面的参数为方法的参数对应Class的对象
userModelClass.getMethod("setUsername", String.class);
```

### 获取成员变量

```java
// 访问公有的成员变量
userModelClass.getField("username");
// 所有已声明的成员变量，但不能得到其父类的成员变量
userModelClass.getDeclaredField("username");
// 返回类或接口声明的所有成员变量，包括公共、保护、默认（包）访问和私有成员变量，但不包括继承的成员变量
userModelClass.getDeclaredFields();
// 返回某个类的所有公有（public）成员变量，包括其继承类的公有成员变量
userModelClass.getFields();
```

### 调用方法

```java
setUsername.invoke(instance, "aa");
```

## Java Hook

通过对 Android 平台的虚拟机注入与 Java 反射的方式，来改变 Android 虚拟机调用函数的方式（ClassLoader），从而达到 Java 函数重定向的目的，这里我们将此类操作称为 Java API Hook。

### Hook 过程

- Hook 的选择点

静态变量和单例，因为一旦创建对象，它们不容易变化，非常容易定位，尽量 Hook public 的对象和方法。

- Hook 过程

选择合适的代理方式，如果是接口可以用动态代理。偷梁换柱——用代理对象替换原始对象。

## Android Hook

从Android的开发来说，Android系统本身就提供给了我们两种开发模式，基于Android SDK的Java语言开发，基于AndroidNDK的Native C/C++语言开发。所以，我们在讨论Hook的时候就必须在两个层面上来讨论。对于Native层来说Hook的难点其实是在理解ELF文件与学习ELF文件上，特别是对ELF文件不太了解的读者来说；对于Java层来说，Hook就需要了解虚拟机的特性与Java上反射的使用。

## Native Hook



## 热修复

### 开源框架

- Tinker

### 类加载器

- PathClassLoader：只能加载安装到手机里面的 dex（比如 data/app/ 包名里面的 dex）
- DexClassLoader：可以加载任意目录下的 dex/jar/apk/zip

## 插件化

## RePlugin 源码



## 参考资料

- [深入解析Java反射（1） - 基础](https://www.sczyh30.com/posts/Java/java-reflection-1/)
- [理解 Android Hook 技术以及简单实战](https://www.jianshu.com/p/4f6d20076922)
- [什么是Hook技术](https://www.kancloud.cn/alex_wsc/android/506821)
- [Android Native Hook技术路线概述](https://gtoad.github.io/2018/07/05/Android-Native-Hook/)

