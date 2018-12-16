# simple-hessian-rpc 简单的hessian rpc框架应用

> 此demo是在spring boot的基础上简单整合了hessian做rpc调用，用于取代httpclient的，简单的分层服务，自动发布接口，可以简单的移植到实际的项目中去。



## 项目构建

​	为了简单起见，并没有分割为不同的项目，而是在一个项目中，搭了三个module，具体应用时，根据不同的应用场景进行定制版改造。

### API

​	此module主要存放一些Entity、VO、Service接口等，方便被其他项目作为jar包进行引用，这个抽离呢，主要考虑交互性的东西。



### Service

​	这里是提供服务的module，引入`API module`的jar包，将`API module`的接口进行实现，并发布，这里需要引用到`Hessian`的依赖，如下：

```xml
<dependency>
	<groupId>com.caucho</groupId>
    <artifactId>hessian</artifactId>
    <version>4.0.38</version>
</dependency>
```

​	注意，hessian需要依赖spring mvc，故，spring mvc的依赖一定要有。

​	以往要通过hessian发布一个接口，采用如下两种方式：

#### xml方式

```xml
<bean id="heroService" class="com.xxx.service.bo.HeroServiceImpl"></bean>

<bean name="/heroService" class="org.springframework.remoting.caucho.HessianServiceExporter">
	<property name="service" ref="heroService"></property>
	<property name="serviceInterface" value="com.xxx.api.HeroService"></property>
</bean>

```



#### @Bean方式

```java
@Autowired
private HeroService heroService;

@Bean(name = "/heroService")
public HessianServiceExporter accountService() {
	HessianServiceExporter exporter = new HessianServiceExporter();
	exporter.setService(heroService);
	exporter.setServiceInterface(HeroService.class);
	return exporter;
}
```

​	其实二者异曲同工，都是将接口进行注入，然后发布到`HessianServiceExporter` ，但是这种方式有一个很不方便的地方，就是我每发布一个接口，都要来这么一段配置，对于开发人员来说，实在是太过费劲，因此呢，下面有一种自动发布的方式，也是这个案例的精髓所在。

#### 自动发布服务

* 首先，定义一个注解，此注解由`@Service`进行派生，然后标注在方法上。如下：

```java
@Target({java.lang.annotation.ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface HessianService {

    String value() default "";
}
```

​	由此注解标注的类，一方面是spring的`@Service`，另一方面，还是hessian要发布的服务。

* 其次，定义一个类，实现`BeanFactoryPostProcessor`，被spring扫描到，然后在实现方法中，将标注`@HessianService`的类进行一一发布。代码如下：

```java
@Component
public class HessianServiceScanner implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        String[] beanNames = beanFactory.getBeanNamesForAnnotation(HessianService.class);

        for (String beanName : beanNames) {
            String className = beanFactory.getBeanDefinition(beanName).getBeanClassName();
            Object bean = beanFactory.getBean(beanName);
            Class<?> clazz = null;
            try {
                clazz = Class.forName(className);
            } catch (ClassNotFoundException e) {
                throw new BeanInitializationException(e.getMessage(), e);
            }
            String hessianService = "/" + beanName.replace("Impl", "");

            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(HessianServiceExporter.class);

            builder.addPropertyReference("service", beanName);
            builder.addPropertyValue("serviceInterface", clazz.getInterfaces()[0].getName());

            ((BeanDefinitionRegistry) beanFactory).registerBeanDefinition(hessianService, builder.getBeanDefinition());
        }
    }
}
```

​	这里需要注意的是：这种方式发布的接口，在客户端调用时，要么是`@HessianService`的value，要么是接口的名称，首字母小写。

* 最后，在实现类上标注`@HessianService`即可。

### Client

​	这里是服务调用的module，引入`API module`的jar包，这里也需要引用到`Hessian`的依赖，具体参考Service里面。

​	此处调用就比较简单了，注入发布的接口，进行调用即可，demo如下：

```java
@Value("${server.url}")
private String url;
@Autowired
private HeroService heroService;

@Bean
public HessianProxyFactoryBean helloClient() {
    HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
    factory.setServiceUrl(url + "/heroService");
    factory.setServiceInterface(HeroService.class);
    return factory;
}

@RequestMapping("/")
public HeroModel index(){
	HeroModel nevermore = heroService.getOneByName("Nevermore");
	System.out.println(nevermore);
	return nevermore;
}
```



## 总结

> Hessian是一个轻量级的remoting onhttp工具，使用简单的方法提供了RMI的功能。 相比WebService，Hessian更简单、快捷。采用的是二进制RPC协议，因为采用的是二进制协议，所以它很适合于发送二进制数据。

选用Hessian主要是看重其比较轻量，不需要去整合Zookeeper等框架技术，而且在一些少量接口的项目中，可以避免频繁使用HTTPClient，这个demo呢，还是有一些瑕疵的，虽然将服务发布进行了自动化，但是客户端调用时，还是需要写大量的`HessianProxyFactoryBean`，这个无法避免。

### 思考

**根据服务自动发布的实现，想了一下客户端是否可用采用类似的方式去自动化呢？**

​	个人觉得是不可以的，当然，也是经过验证了的，因为在项目启动的时候，注入发布的接口时，如果没有去注入`HessianProxyFactoryBean`，则spring会发现，发布的接口没有实现类，无法去注入，而这里呢，好像又产生了循环依赖，所以呢，需要解决这样的问题，以当前项目为例，在spring启动的时候，先去查找需要serviceUrl，然后让`HessianProxyFactoryBean` 进行注入，而后再在使用到`HeroService`地方注入。

​	语言比较混乱，而且对spring的源码不太熟悉，可能表达的就有误吧，见仁见智。