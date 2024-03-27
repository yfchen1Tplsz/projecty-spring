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
import org.springframework.context.expression.StandardBeanExpressionResolver;
import org.springframework.core.AliasRegistry;
import org.springframework.core.ResolvableType;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.format.support.DefaultFormattingConversionService;

import java.util.Date;
import java.util.Map;
import java.util.Set;

/**
 * @author yfchen1
 * @date 2024/3/26
 * @apiNote 用于展示DefaultListableBeanFactory所继承而来的接口能力 --Ioc核心容器接口之BeanFactory类族
 */
public class Order01_DefaultListableBeanFactoryInterface {
    private static Logger log = LoggerFactory.getLogger(Order01_DefaultListableBeanFactoryInterface.class);

    public static void main(String[] args) {
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
            //6.1 HierarchicalBeanFactory接口能力之一：获取父级容器
            BeanFactory parentBeanFactory = hierarchicalBeanFactory.getParentBeanFactory();
            //6.2 HierarchicalBeanFactory接口能力之二：判断这个BeanName是否被当前容器使用（单例、BeanDefinition、factoryBeanName..）
            boolean isUsedByLocal = hierarchicalBeanFactory.containsLocalBean("sampleClass04");
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 6. 枚举所有Bean实例接口 -> ListableBeanFactory, 定义了一系列可以枚举出所有Bean实例的接口方法，
         * 其接口能力主要由 StaticListableBeanFactory实现，再由DefaultListableBeanFactory补充实现，实现主要依靠StaticListableBeanFactory成员变量Map<String, Object> beans 完成 */
        if (beanFactory instanceof ListableBeanFactory listableBeanFactory) {
            //5.1 ListableBeanFactory接口能力之一：根据指定的类型获取所有Bean的名称数组，参数包含是否包含非单例、是否允许提前初始化BeanProvider、FactoryBean内的Bean，默认都是true
            String[] beanNamesForType = listableBeanFactory.getBeanNamesForType(SampleInterface.class);
            String[] beanNamesForTypeFilterNonSingleton = listableBeanFactory.getBeanNamesForType(SampleInterface.class, false, true);
            //5.2 ListableBeanFactory接口能力之二：根据指定ResolvableType获取所有Bean的名称数组，参数同5.1，功能也基本同
            String[] beanNamesForTypeByResolvableType = listableBeanFactory.getBeanNamesForType(ResolvableType.forRawClass(SampleInterface.class));
            String[] beanNamesForTypeByResolvableTypeFilterNonSingleton = listableBeanFactory.getBeanNamesForType(ResolvableType.forRawClass(SampleInterface.class), false, true);
            //5.3 ListableBeanFactory接口能力之三：根据指定类型获取所有match这个类型的Bean实例组成BeanName为key，Bean实例为Value的Map,同样是两个参数，是否包含非单例和是否允许提前初始化
            Map<String, SampleInterface> sampleInterfaceMap = listableBeanFactory.getBeansOfType(SampleInterface.class);
            Map<String, SampleInterface> sampleInterfaceMapFilterNonSingleton = listableBeanFactory.getBeansOfType(SampleInterface.class, false, true);
            //5.4 ListableBeanFactory接口能力之四： 根据指定的注解类型获取类上标注有该注解的BeanName（仅仅是标注在类级别上）
            String[] beanNamesForAnnotation = listableBeanFactory.getBeanNamesForAnnotation(Data.class);
            //5.5 ListableBeanFactory接口能力之五： 根据指定的注解类型获取类上标注有该注解的Bean实例组成的Map<String，Object>
            Map<String, Object> beansWithAnnotation = listableBeanFactory.getBeansWithAnnotation(Data.class);
            //5.6 ListableBeanFactory接口能力之六： 根据BeanName以及注解类型，去获取标注在这个Bean类的注解实例，同时也有参数标识是否允许提前初始化,默认允许
            MyFlag myFlag1 = listableBeanFactory.findAnnotationOnBean("sampleClass04", MyFlag.class);
            MyFlag myFlag2 = listableBeanFactory.findAnnotationOnBean("sampleClass04", MyFlag.class, false);
            //5.7 ListableBeanFactory接口能力之七：根据BeanName以及注解类型，去获取标注在这个Bean类、以及所实现的接口、继承的类上的所有注解实例，并封装为set,第三个参数是是否允许提前初始化
            Set<MyFlag> annotationSet = listableBeanFactory.findAllAnnotationsOnBean("sampleClass04", MyFlag.class, false);
        }
        /**-------------------------------------------------------------------------------------------------------------------------------------*/
        /* 7. BeanFactory通用配置能力 ->ConfigurableBeanFactory，
         * 定义了类加载器、类型转换、属性编辑、后处理器、作用域、Bean依赖关系处理、销毁Bean等接口方法，
         * 主要由AbstractBeanFactory实现能力 */
        if (beanFactory instanceof ConfigurableBeanFactory configurableBeanFactory) {
            //6.1 ConfigurableBeanFactory接口能力之一： 设置父容器
            configurableBeanFactory.setParentBeanFactory(new DefaultListableBeanFactory());
            //6.2 ConfigurableBeanFactory接口能力之二： 设置类加载器
            configurableBeanFactory.setBeanClassLoader(Thread.currentThread().getContextClassLoader());
            //6.3 ConfigurableBeanFactory接口能力之三： 获取类加载器
            ClassLoader beanClassLoader = configurableBeanFactory.getBeanClassLoader();
            //6.4 ConfigurableBeanFactory接口能力之四： 设置临时的类加载器
            configurableBeanFactory.setTempClassLoader(null);
            //6.5 ConfigurableBeanFactory接口能力之五： 获取临时的类加载器
            ClassLoader tempClassLoader = configurableBeanFactory.getTempClassLoader();
            //6.6 ConfigurableBeanFactory接口能力之六： 设置是否缓存Bean的元数据Metadata
            configurableBeanFactory.setCacheBeanMetadata(true);
            //6.7 ConfigurableBeanFactory接口能力之七： 获取当前是否缓存Bean元数据的配置
            boolean isCacheMetadata = configurableBeanFactory.isCacheBeanMetadata();
            //6.8 ConfigurableBeanFactory接口能力之八： 设置容器的Spel表达式解析器（BeanExpressionResolver接口）
            // -- spring默认提供了 StandardBeanExpressionResolver
            configurableBeanFactory.setBeanExpressionResolver(new StandardBeanExpressionResolver());
            //6.9 ConfigurableBeanFactory接口方法之九： 获取容器内SpEL表达式解析器
            BeanExpressionResolver beanExpressionResolver = configurableBeanFactory.getBeanExpressionResolver();
            //6.10 ConfigurableBeanFactory接口方法之十：设置容器内的类型转换器（上层类型转换器接口ConversionService）
            configurableBeanFactory.setConversionService(new DefaultFormattingConversionService());
            //6.11 ConfigurableBeanFactory接口方法之十一： 获取容器内的类型转换器
            ConversionService conversionService = configurableBeanFactory.getConversionService();
            //6.12 ConfigurableBeanFactory接口方法之十二： 添加容器内的属性编辑器（属性绑定）
            configurableBeanFactory.addPropertyEditorRegistrar(new ResourceEditorRegistrar(new DefaultResourceLoader(), null));
            //6.13 ConfigurableBeanFactory接口方法之十三： 注册自定义的属性编辑器到容器中（Spring默认不提供）
            configurableBeanFactory.registerCustomEditor(Date.class, MyCustomDateEditor.class);
            //6.14 ConfigurableBeanFactory接口方法之十四： 将容器中注册的属性编辑器copy到参数的属性编辑器中
            configurableBeanFactory.copyRegisteredEditorsTo(new SimpleTypeConverter());
            //6.15 ConfigurableBeanFactory接口方法之十五： 设置Spring类型转换底层转换器TypeConverter
            configurableBeanFactory.setTypeConverter(new SimpleTypeConverter());
            //6.16 ConfigurableBeanFactory接口方法之十六： 获取Spring类型转换底层转换器TypeConverter
            TypeConverter typeConverter = configurableBeanFactory.getTypeConverter();
            //6.17 ConfigurableBeanFactory接口方法之十七： 添加嵌入值解析器（如解析${}）
            configurableBeanFactory.addEmbeddedValueResolver(strValue -> new StandardEnvironment().resolvePlaceholders(strValue));
            //6.18 ConfigurableBeanFactory接口方法之十八： 判断容器中是否存在嵌入的值解析器
            boolean hasEmbeddedValueResolver = configurableBeanFactory.hasEmbeddedValueResolver();
            //6.19 ConfigurableBeanFactory接口方法之十九： 获取容器中注册的BeanPostProcessor数量
            int beanPostProcessorCount = configurableBeanFactory.getBeanPostProcessorCount();
            //6.20 ConfigurableBeanFactory接口方法之二十： 向容器中添加BeanPostProcessor
            configurableBeanFactory.addBeanPostProcessor(new AutowiredAnnotationBeanPostProcessor());
            //6.21 ConfigurableBeanFactory接口方法之二十一：向容器中注册一个新的声明周期Scope(默认有Singleton、Prototype、Request、Session、Application)
            configurableBeanFactory.registerScope("custom", new CustomRequestScope());
            //6.22 ConfigurableBeanFactory接口方法之二十二： 根据ScopeName获取容器中存在的Scope对象
            Scope scope = configurableBeanFactory.getRegisteredScope("custom");
            //6.23 ConfigurableBeanFactory接口方法之二十三： 获取容器中所有ScopeName数组
            String[] registeredScopeNames = configurableBeanFactory.getRegisteredScopeNames();
            

        }

    }
}
