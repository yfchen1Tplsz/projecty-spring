## Read Me

### TIPS:

​	这个project将会记录我对于spring学习的一些笔记与代码片段，希望对你也有所帮助。

​									-- by yfchen1

### CONTENT:

##### 一、Spring之Ioc容器：

- DefaultListableBeanFactory的底层接口能力
- BeanFactory与ApplicationContext关系
- 常见的BeanFactoryPostProcessor与BeanPostProcessor
- ApplicationContext的一些实现
- 自定义PostProcessor模拟实现@Autowired、@Value、@ComponentScan的注解解析
- ApplicationContext的refresh()方法内部流程

##### 二、Spring之Aop代理：

- 常见的三种aop实现
  - ajc编译增强实现
  - agent类加载增强实现
  - jdk/cglib代理增强实现
- jdk代理之使用与原理
- cglib代理之使用与原理
- spring整合代理的策略
- spring中aop的流程

##### 三、Spring MVC介绍

- web环境的准备
- DispatcherServlet的初始化流程
- HandlerMapping
- HandlerAdapter
- 参数解析器
- 数据类型转换与对象绑定
- 返回值处理器
- MessageConverter
- 异常处理&Tomcat异常
- ControllerAdvice之全局增强
- MVC初始化与执行流程

##### 四、SpringBoot 自动化配置

- SpringApplication对象创建流程

- SpringApplication之run方法执行流程

- 内嵌Tomcat整合

- 自动装配原理

- 条件装配原理

  
