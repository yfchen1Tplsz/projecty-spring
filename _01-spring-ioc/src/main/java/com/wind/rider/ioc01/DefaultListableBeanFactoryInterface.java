package com.wind.rider.ioc01;

import com.wind.rider.ioc01.infrastructure.annotation.MyFlag;
import com.wind.rider.ioc01.infrastructure.componet.CustomRequestScope;
import com.wind.rider.ioc01.infrastructure.componet.MyCustomDateEditor;
import com.wind.rider.ioc01.infrastructure.sample.*;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.SimpleTypeConverter;
import org.springframework.beans.TypeConverter;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.HierarchicalBeanFactory;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.AutowiredAnnotationBeanPostProcessor;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.beans.support.ResourceEditorRegistrar;
import org.springframework.boot.context.metrics.buffering.BufferingApplicationStartup;
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.AliasRegistry;
import org.springframework.core.MethodParameter;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.metrics.ApplicationStartup;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author yfchen1
 * @date 2024/3/26
 * @apiNote 用于展示DefaultListableBeanFactory所继承而来的接口能力 --Ioc核心容器接口之BeanFactory类族
 */
public class DefaultListableBeanFactoryInterface {
    private static Logger log = LoggerFactory.getLogger(DefaultListableBeanFactoryInterface.class);

    public static void main(String[] args) throws NoSuchMethodException {
        /* 0. ctrl + alt + U 快速查看类图 */
        Object beanFactory = new DefaultListableBeanFactory();
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 1. 别名控制接口 -> AliasRegistry ,定义了一系列对Bean的别名管理的接口方法，
         * 其接口能力主要由SimpleAliasRegistry实现，通过其成员变量Map<String,String> aliasMap映射别名与Bean名，并不存在Bean */
        if (beanFactory instanceof AliasRegistry aliasRegistry) {
            //1.1 AliasRegistry接口方法之一：注册Bean的别名
            aliasRegistry.registerAlias("bean1", "beanOne");
            //1.2 AliasRegistry接口方法之二：获取Bean的别名数组
            String[] aliasArray = aliasRegistry.getAliases("bean1");
            //1.3 AliasRegistry接口方法之三：移除Bean的某个别名
            aliasRegistry.removeAlias("beanOne");
            //1.4 AliasRegistry接口方法之四：判断一个字符串是否为任一个Bean的别名
            boolean isAlias = aliasRegistry.isAlias("beanOne");
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 2. 单例Bean注册与查询接口 ->SingletonBeanRegistry, 定义了对单例Bean的注册与查询的接口方法，
         *  其接口能力主要是由 DefaultSingletonBeanRegistry 实现，单例Bean存储于其成员变量Map<String,Object> singletonObjects */
        if (beanFactory instanceof SingletonBeanRegistry singletonBeanRegistry) {
            //2.1 SingletonBeanRegistry接口方法之一：注册单例Bean，值得注意的是这里的Bean是以成品Bean形式放入容器，不会再进行初始化、依赖注入、后处理流程
            singletonBeanRegistry.registerSingleton("sampleClass01", new SampleClass01());
            //2.2 SingletonBeanRegistry接口方法之二：获取容器中单例Bean的数量
            int singletonCount = singletonBeanRegistry.getSingletonCount();
            //2.3 SingletonBeanRegistry接口方法之三：根据Bean的名称判断容器中是否存在某个单例Bean
            boolean isContainsSingleton = singletonBeanRegistry.containsSingleton("sampleClass01");
            //2.4 SingletonBeanRegistry接口能力之四：根据Bean的名称获取容器中某个单例Bean
            SampleClass01 sampleClass01 = (SampleClass01) singletonBeanRegistry.getSingleton("sampleClass01");
            //2.5 SingletonBeanRegistry接口能力之五：获取容器中所有单例Bean的名称数组
            String[] singletonNameArray = singletonBeanRegistry.getSingletonNames();
            //2.6 SingletonBeanRegistry接口能力之六：获取容器的互斥锁，本质上是直接获取DefaultSingletonBeanRegistry中的singletonObjects作为互斥锁synchronized
            Object singletonMutex = singletonBeanRegistry.getSingletonMutex();
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 3. 注册BeanDefinition接口 -> BeanDefinitionRegistry, 定义了向ioc容器中注册BeanDefinition(bean定义信息)的接口方法，
         * 其接口能力主要由 SimpleBeanDefinitionRegistry\ DefaultListableBeanFactory(实际走的) 实现，依靠的是SimpleBeanDefinitionRegistry的成员变量Map<String, BeanDefinition> beanDefinitionMap */
        if (beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry) {
            //3.1 BeanDefinitionRegistry 接口方法之一：注册BeanDefinition 单例/多例
            AbstractBeanDefinition sample02BeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SampleClass02.class)
                    .setScope("singleton")
                    .getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition("sampleClass02", sample02BeanDefinition);
            AbstractBeanDefinition sample03BeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SampleClass03.class)
                    .setScope("prototype")
                    .setInitMethodName("init")
                    .getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition("sampleClass03", sample03BeanDefinition);
            AbstractBeanDefinition sample04BeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SampleClass04.class)
                    .setScope("prototype")
                    .setInitMethodName("initialize")
                    .getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition("sampleClass04", sample04BeanDefinition);
            AbstractBeanDefinition sample05BeanDefinition = BeanDefinitionBuilder.genericBeanDefinition(SampleClass05.class)
                    .setScope("singleton")
                    .getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition("sampleClass05", sample05BeanDefinition);
            //3.2 BeanDefinitionRegistry接口方法之二：根据Bean的名称判断容器中是否存在该Bean的BeanDefinition
            boolean isSample02BeanDefinitionExist = beanDefinitionRegistry.containsBeanDefinition("sampleClass02");
            //3.3 BeanDefinitionRegistry接口方法之三：根据Bean的名称获取BeanDefinition
            BeanDefinition sample01BeanDefinition = beanDefinitionRegistry.getBeanDefinition("sampleClass02");
            //3.4 BeanDefinitionRegistry接口方法之四：获取容器中BeanDefinition的数量
            int beanDefinitionCount = beanDefinitionRegistry.getBeanDefinitionCount();
            //3.5 BeanDefinitionRegistry接口方法之五：获取容器中所有BeanDefinition的名称数组
            String[] beanDefinitionNames = beanDefinitionRegistry.getBeanDefinitionNames();
            //3.6 BeanDefinitionRegistry接口方法之六：根据Bean的名称来判断对应的BeanDefinition是否允许被覆盖
            boolean isAllowOverride = beanDefinitionRegistry.isBeanDefinitionOverridable("sampleClass02");
            //3.7 BeanDefinitionRegistry接口方法之七：判断一个Bean的名字是否作为别名或者BeanDefinition的BeanName使用中
            boolean isBeanNameUse = beanDefinitionRegistry.isBeanNameInUse("sampleClass02");
            //3.8 BeanDefinitionRegistry接口方法之八：根据Bean的名称获取容器中BeanDefinition
            beanDefinitionRegistry.removeBeanDefinition("sampleClass02");
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 4. 容器中获取Bean接口 -> BeanFactory，定义了从容器中获取Bean的一系列接口方法
         * 其接口能力主要由 AbstractBeanFactory 实现，主要通过DefaultSingletonBeanRegistry成员变量singletonObjects 与 SimpleBeanDefinitionRegistry成员变量 beanDefinitionMap完成逻辑*/
        if (beanFactory instanceof BeanFactory factory) {
            //4.1 BeanFactory接口方法之一： 根据BeanName从Ioc容器中获取Object类型的Bean
            Object sampleClass01 = factory.getBean("sampleClass01");
            //4.2 BeanFactory接口方法之二： 根据Bean的Class类型 从IOC容器中获取对应类型的Bean
            SampleClass03 sampleClass031 = factory.getBean(SampleClass03.class);
            //4.3 BeanFactory接口方法之三： 更具BeanName从Ioc容器中获取Bean，如果Bean的类型不是requiredType则抛出BeanNotOfRequiredTypeException异常
            SampleClass03 sampleClass032 = factory.getBean("sampleClass03", SampleClass03.class);
            //4.4 BeanFactory接口方法之四：根据BeanName从Ioc容器中获取BeanDefinition，根据这个BeanDefiniiton与传入的参数创还能出Bean并返回
            //  -- 如果BeanDefinition不是prototype，，并且容器中已经有了Bean，则会直接返回这个Bean，参数无效
            //  -- 如果不存在BeanName对应的BeanDefinition则会抛出NoSuchBeanDefinitionException
            //  -- 如果无法创建Bean，则会抛出BeansException
            SampleClass04 simpleClass041 = (SampleClass04) factory.getBean("sampleClass04", "MSG:1001");
            //4.5 BeanFactory接口方法之五：根据RequiredType从ioc容器中获取BeanDefinition并根据参数创建出Bean并返回，规则同4.4
            SampleClass04 simpleClass042 = factory.getBean(SampleClass04.class, "MSG:2000");
            //4.6 BeanFactory接口能力之六： 根据BeanName判断容器中是否存在该Bean
            boolean isSampleClass04Exist = factory.containsBean("sampleClass04");
            //4.7 BeanFactory接口能力之七： 根据BeanName判断容器中该Bean是否单例/原型Bean
            boolean isSampleClass04Singleton = factory.isSingleton("sampleClass04");
            boolean isSampleClass04Prototype = factory.isPrototype("sampleClass04");
            //4.8 BeanFactory接口能力之八： 根据BeanName获取该Bean的类型,如果Bean是FactoryBean的类型，则获取它getBean后的类型，默认允许初始化FactoryBean
            Class<?> sampleClass03Clazz1 = factory.getType("sampleClass03");
            Class<?> sampleClass03Clazz2 = factory.getType("sampleClass03", false);
            //4.9 BeanFactory接口能力之九： 根据RequiredType/ResolvableType从Ioc容器中获取一个类型安全的ObjectProvider，允许其懒加载
            ObjectProvider<SampleClass04> sampleClass04ObjectProvider = factory.getBeanProvider(SampleClass04.class);
            SampleClass04 sampleClass04 = sampleClass04ObjectProvider.getIfAvailable(() -> new SampleClass04("default-object"));
            ObjectProvider<Object> beanProvider = factory.getBeanProvider(ResolvableType.forRawClass(SampleClass03.class));
            SampleClass03 sampleClass03 = (SampleClass03) beanProvider.getIfAvailable(() -> new SampleClass03());
            //4.10 BeanFactory接口能力之十：根据RequiredType/ResolvableType判断容器中BeanName对应的Bean是否匹配类型是指定类型的子类
            boolean isMatch01 = factory.isTypeMatch("sampleClass03", SampleClass03.class);
            boolean isMatch02 = factory.isTypeMatch("sampleClass04", ResolvableType.forRawClass(SampleClass04.class));
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 5. 父子容器接口 ->HierarchicalBeanFactory ,定义了获取父级容器的接口，
         *  其接口主要由AbstractBeanFactory实现，实现手段为其成员变量parentBeanFactory ，但是在SpringBoot中一般是不会出现容器的父级关系的 */
        if (beanFactory instanceof HierarchicalBeanFactory hierarchicalBeanFactory) {
            //5.1 HierarchicalBeanFactory接口能力之一：获取父级容器
            BeanFactory parentBeanFactory = hierarchicalBeanFactory.getParentBeanFactory();
            //5.2 HierarchicalBeanFactory接口能力之二：判断这个BeanName是否被当前容器使用（单例、BeanDefinition、factoryBeanName..）
            boolean isUsedByLocal = hierarchicalBeanFactory.containsLocalBean("sampleClass04");
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 6. 枚举所有Bean实例接口 -> ListableBeanFactory, 定义了一系列可以枚举出所有Bean实例的接口方法，
         * 其接口能力主要由 StaticListableBeanFactory实现，再由DefaultListableBeanFactory补充实现，实现主要依靠StaticListableBeanFactory成员变量Map<String, Object> beans 完成 */
        if (beanFactory instanceof ListableBeanFactory listableBeanFactory) {
            //6.1 ListableBeanFactory接口能力之一：根据指定的类型获取所有Bean的名称数组，参数包含是否包含非单例、是否允许提前初始化BeanProvider、FactoryBean内的Bean，默认都是true
            String[] beanNamesForType = listableBeanFactory.getBeanNamesForType(SampleInterface.class);
            String[] beanNamesForTypeFilterNonSingleton = listableBeanFactory.getBeanNamesForType(SampleInterface.class, false, true);
            //6.2 ListableBeanFactory接口能力之二：根据指定ResolvableType获取所有Bean的名称数组，参数同5.1，功能也基本同
            String[] beanNamesForTypeByResolvableType = listableBeanFactory.getBeanNamesForType(ResolvableType.forRawClass(SampleInterface.class));
            String[] beanNamesForTypeByResolvableTypeFilterNonSingleton = listableBeanFactory.getBeanNamesForType(ResolvableType.forRawClass(SampleInterface.class), false, true);
            //6.3 ListableBeanFactory接口能力之三：根据指定类型获取所有match这个类型的Bean实例组成BeanName为key，Bean实例为Value的Map,同样是两个参数，是否包含非单例和是否允许提前初始化
            Map<String, SampleInterface> sampleInterfaceMap = listableBeanFactory.getBeansOfType(SampleInterface.class);
            Map<String, SampleInterface> sampleInterfaceMapFilterNonSingleton = listableBeanFactory.getBeansOfType(SampleInterface.class, false, true);
            //6.4 ListableBeanFactory接口能力之四： 根据指定的注解类型获取类上标注有该注解的BeanName（仅仅是标注在类级别上）
            String[] beanNamesForAnnotation = listableBeanFactory.getBeanNamesForAnnotation(Data.class);
            //6.5 ListableBeanFactory接口能力之五： 根据指定的注解类型获取类上标注有该注解的Bean实例组成的Map<String，Object>
            Map<String, Object> beansWithAnnotation = listableBeanFactory.getBeansWithAnnotation(Data.class);
            //6.6 ListableBeanFactory接口能力之六： 根据BeanName以及注解类型，去获取标注在这个Bean类的注解实例，同时也有参数标识是否允许提前初始化,默认允许
            MyFlag myFlag1 = listableBeanFactory.findAnnotationOnBean("sampleClass04", MyFlag.class);
            MyFlag myFlag2 = listableBeanFactory.findAnnotationOnBean("sampleClass04", MyFlag.class, false);
            //6.7 ListableBeanFactory接口能力之七：根据BeanName以及注解类型，去获取标注在这个Bean类、以及所实现的接口、继承的类上的所有注解实例，并封装为set,第三个参数是是否允许提前初始化
            Set<MyFlag> annotationSet = listableBeanFactory.findAllAnnotationsOnBean("sampleClass04", MyFlag.class, false);
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 7. 运行时创建、配置、处理Bean实例能力 ->AutowireCapableBeanFactory ,
        * 定义了创建Bean实例、自动装配依赖、对已存在对象进行依赖注入、应用BeanPostProcessor、解析依赖等接口方法
        * 主要由AbstractAutowireCapableBeanFactory进行实现 */
        if(beanFactory instanceof AutowireCapableBeanFactory autowireCapableBeanFactory){
            //7.1 AutowireCapableBeanFactory接口能力之一：根据指定类型创建Bean实例,默认prototype且走完成bean创建创建周期（初始化、applyPostProcessor等）
            SampleClass02 sampleClass02 = autowireCapableBeanFactory.createBean(SampleClass02.class);
            //7.2 AutowireCapableBeanFactory接口能力之二：根据对象的类型和配置信息(注解)，从容器中自动装配所需的依赖
            SampleClass05 sampleClass05 = new SampleClass05();
            autowireCapableBeanFactory.autowireBean(sampleClass05);
            //7.3 AutowireCapableBeanFactory接口能力之三：对于已经实例化的Bean根据对应beanName的BeanDefinition对Bean进行一系列后续操作，包括自动装配 bean 属性、应用 bean 属性值、应用工厂回调（如 setBeanName 和 setBeanFactory）、以及应用所有 bean 后处理器
            Object sampleClass03 = autowireCapableBeanFactory.configureBean(new SampleClass03(), "sampleClass03");
            //7.4 AutowireCapableBeanFactory接口能力之四：根据指定类型完成依赖自动装配并创建返回相对的Bean实例，可以选择装配方式、是否进行依赖检查
            Object autowire = autowireCapableBeanFactory.autowire(SampleClass05.class, AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE, false);
            //7.5 AutowireCapableBeanFactory接口能力之五：对现有对象进行属性注入，创建BeanDefinition，默认多例
            autowireCapableBeanFactory.autowireBeanProperties(sampleClass05,AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE,true);
            //7.6 AutowireCapableBeanFactory接口能力之六：将属性值应用到其中的现有的 bean 实例，beanName指定BeanDefinition
            autowireCapableBeanFactory.applyBeanPropertyValues(sampleClass03,"sampleClass03");
            //7.7 AutowireCapableBeanFactory接口能力之七：对已经存在的Bean实例进行初始化，根据beanName找到对应BeanDefinition
            Object sample03AfterInit = autowireCapableBeanFactory.initializeBean(sampleClass03, "sampleClass03");
            //7.8 AutowireCapableBeanFactory接口能力之八：销毁已经存在的Bean
            autowireCapableBeanFactory.destroyBean(sampleClass03);
            //7.9 AutowireCapableBeanFactory接口能力之九：根据指定类型返回带有 bean 名称、Bean实例的持有者对象
            NamedBeanHolder<SampleClass03> namedBeanHolder = autowireCapableBeanFactory.resolveNamedBean(SampleClass03.class);
            //7.10 AutowireCapableBeanFactory接口能力之十：用于按照给定的名称解析 bean，并根据依赖描述符返回相应的 bean 实例，用于解决依赖注入的场景,name指的是要被解析Bean名
            DependencyDescriptor dd1 = new DependencyDescriptor(new MethodParameter(SampleClass05.class.getDeclaredMethod("setSampleClass01", SampleClass01.class), 0), false);
            Object sampleClass01 = autowireCapableBeanFactory.resolveBeanByName("sampleClass01", dd1);
            //7.11 AutowireCapableBeanFactory接口能力之十一：用于按照给定的名称解析 bean，并根据依赖描述符返回相应的 bean 实例，第二个参数requestingBeanName表示解析后作为Bean的被注入目标BeanName
            Object sampleClass011 = autowireCapableBeanFactory.resolveDependency(dd1, "sampleClass01");
            //7.12 AutowireCapableBeanFactory接口能力之十二：扩展7.11注入，添加类型转换、依赖注入选择优先考虑BeanNames
            Object o = autowireCapableBeanFactory.resolveDependency(dd1, "sampleClass001", new HashSet<>(), null);

        }

        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 8. BeanFactory通用配置能力 ->ConfigurableBeanFactory，
         * 定义了类加载器、类型转换、属性编辑、后处理器、作用域、Bean依赖关系处理、销毁Bean等接口方法，
         * 主要由AbstractBeanFactory实现能力 */
        if (beanFactory instanceof ConfigurableBeanFactory configurableBeanFactory) {
            //8.1 ConfigurableBeanFactory接口能力之一： 设置父容器
            configurableBeanFactory.setParentBeanFactory(new DefaultListableBeanFactory());
            //8.2 ConfigurableBeanFactory接口能力之二： 设置类加载器
            configurableBeanFactory.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
            //8.3 ConfigurableBeanFactory接口能力之三： 获取类加载器
            ClassLoader beanClassLoader = configurableBeanFactory.getBeanClassLoader();
            //8.4 ConfigurableBeanFactory接口能力之四： 设置临时的类加载器
            configurableBeanFactory.setTempClassLoader(null);
            //8.5 ConfigurableBeanFactory接口能力之五： 获取临时的类加载器
            ClassLoader tempClassLoader = configurableBeanFactory.getTempClassLoader();
            //8.6 ConfigurableBeanFactory接口能力之六： 设置是否缓存Bean的元数据Metadata
            configurableBeanFactory.setCacheBeanMetadata(true);
            //8.7 ConfigurableBeanFactory接口能力之七： 获取当前是否缓存Bean元数据的配置
            boolean isCacheMetadata = configurableBeanFactory.isCacheBeanMetadata();
            //8.8 ConfigurableBeanFactory接口能力之八： 设置容器的Spel表达式解析器（BeanExpressionResolver接口）
            // -- spring默认提供了 StandardBeanExpressionResolver
            configurableBeanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
            //8.9 ConfigurableBeanFactory接口方法之九： 获取容器内SpEL表达式解析器
            BeanExpressionResolver beanExpressionResolver = configurableBeanFactory.getBeanExpressionResolver();
            //8.10 ConfigurableBeanFactory接口方法之十：设置容器内的类型转换器（上层类型转换器接口ConversionService）
            configurableBeanFactory.setConversionService(new DefaultFormattingConversionService());
            //8.11 ConfigurableBeanFactory接口方法之十一： 获取容器内的类型转换器
            ConversionService conversionService = configurableBeanFactory.getConversionService();
            //8.12 ConfigurableBeanFactory接口方法之十二： 添加容器内的属性编辑器（属性绑定）
            configurableBeanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(new DefaultResourceLoader(), null));
            //8.13 ConfigurableBeanFactory接口方法之十三： 注册自定义的属性编辑器到容器中（Spring默认不提供）
            configurableBeanFactory.registerCustomEditor(Date.class, MyCustomDateEditor.class);
            //8.14 ConfigurableBeanFactory接口方法之十四： 将容器中注册的属性编辑器copy到参数的属性编辑器中
            configurableBeanFactory.copyRegisteredEditorsTo(new SimpleTypeConverter());
            //8.15 ConfigurableBeanFactory接口方法之十五： 设置Spring类型转换底层转换器TypeConverter
            configurableBeanFactory.setTypeConverter(new SimpleTypeConverter());
            //8.16 ConfigurableBeanFactory接口方法之十六： 获取Spring类型转换底层转换器TypeConverter
            TypeConverter typeConverter = configurableBeanFactory.getTypeConverter();
            //8.17 ConfigurableBeanFactory接口方法之十七： 添加嵌入值解析器（如解析${}）
            configurableBeanFactory.addEmbeddedValueResolver(strValue -> new StandardEnvironment().resolvePlaceholders(strValue));
            //8.18 ConfigurableBeanFactory接口方法之十八： 判断容器中是否存在嵌入的值解析器
            boolean hasEmbeddedValueResolver = configurableBeanFactory.hasEmbeddedValueResolver();
            //8.19 ConfigurableBeanFactory接口方法之十九： 获取容器中注册的BeanPostProcessor数量
            int beanPostProcessorCount = configurableBeanFactory.getBeanPostProcessorCount();
            //8.20 ConfigurableBeanFactory接口方法之二十： 向容器中添加BeanPostProcessor
            configurableBeanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
            //8.21 ConfigurableBeanFactory接口方法之二十一：向容器中注册一个新的声明周期Scope(默认有Singleton、Prototype、Request、Session、Application)
            configurableBeanFactory.registerScope("custom", new CustomRequestScope());
            //8.22 ConfigurableBeanFactory接口方法之二十二： 根据ScopeName获取容器中存在的Scope对象
            Scope scope = configurableBeanFactory.getRegisteredScope("custom");
            //8.23 ConfigurableBeanFactory接口方法之二十三： 获取容器中所有ScopeName数组
            String[] registeredScopeNames = configurableBeanFactory.getRegisteredScopeNames();
            //8.24 ConfigurableBeanFactory接口方法之二十四： 设置一个启动步骤追踪对象，可以被ApplicationStartupAware监听，常用于启动性能追踪、为bean提供追踪能力、细粒度步骤追踪
            configurableBeanFactory.setApplicationStartup(new BufferingApplicationStartup(100));
            //8.25 ConfigurableBeanFactory接口方法之二十五： 获取容器中启动步骤追踪对象
            ApplicationStartup applicationStartup = configurableBeanFactory.getApplicationStartup();
            //8.26 ConfigurableBeanFactory接口方法之二十六： Ioc容器拷贝
            ((ConfigurableBeanFactory)new DefaultListableBeanFactory()).copyConfigurationFrom(configurableBeanFactory);
            //8.27 ConfigurableBeanFactory接口方法之二十七： 使用StringValueResolver遍历别名和对应的bean名进行解析后放回aliasMap
            configurableBeanFactory.resolveAliases(strValue -> new StandardEnvironment().resolvePlaceholders(strValue));
            //8.28 ConfigurableBeanFactory接口方法之二十八： 根据BeanName获取父子BeanDefinition合并后的BeanDefinition（加载Bean、初始化Bean后合并的BeanDefinition）
            BeanDefinition sampleClass04 = configurableBeanFactory.getMergedBeanDefinition("sampleClass04");
            //8.29 ConfigurableBeanFactory接口方法之二十九： 根据BeanName判断一个Bean是否为BeanFactory类型
            boolean isFactoryBean = configurableBeanFactory.isFactoryBean("sampleClass04");
            //8.30 ConfigurableBeanFactory接口方法之三十： 设置Bean是否正在被创建过程中
            configurableBeanFactory.setCurrentlyInCreation("sampleClass03",true);
            //8.31 ConfigurableBeanFactory接口方法之三十一：根据BeanName判断这个Bean是否正在被创建忠
            boolean isCurrentlyInCreation = configurableBeanFactory.isCurrentlyInCreation("sampleClass03");
            //8.32 ConfigurableBeanFactory接口方法之三十二： 向容器注册Bean之前的依赖关系，后者依赖前者，前者销毁后者也随之销毁
            configurableBeanFactory.registerDependentBean("sampleClass03","sampleClass04");
            //8.33 ConfigurableBeanFactory接口方法之三十三： 根据BeanName获取依赖于指定 bean 的所有其他 bean 的名称
            String[] dependentBeans = configurableBeanFactory.getDependentBeans("sampleClass03");
            //8.34 ConfigurableBeanFactory接口方法之三十四： 根据BeanName获取指定Bean所依赖的所有Bean的名称
            String[] dependenciesForBean = configurableBeanFactory.getDependenciesForBean("sampleClass04");
            //8.35 ConfigurabelBeanFactory接口方法之三十五： 销毁Bean实例
            configurableBeanFactory.destroyBean("sampleClass03",configurableBeanFactory.getBean("sampleClass03"));
            //8.36 ConfigurableBeanFactory接口方法之三十六： 销毁Scope实例
            //   configurableBeanFactory.destroyScopedBean("sampleClass03");
            //8.37 ConfigurableBeanFactory接口方法之三十七： 销毁所有单例Bean
            configurableBeanFactory.destroySingletons();
        }

    }
}
