package com.wind.rider.ioc02;

import com.google.common.base.Strings;
import com.wind.rider.ioc01.DefaultListableBeanFactoryInterface;
import com.wind.rider.ioc02.infrastructure.config.ConfigClass01;
import com.wind.rider.ioc02.infrastructure.sample.SampleBean01;
import com.wind.rider.ioc02.infrastructure.sample.SampleBean02;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.annotation.AnnotationConfigUtils;

/**
 * @author yfchen1
 * @date 2024/3/28
 * @apiNote  演示常见的BeanPostProcessor
 */
public class Order01_LoadPostProcessor {
    private static Logger log = LoggerFactory.getLogger(Order01_LoadPostProcessor.class);
    public static void main(String[] args) {
        /*1. 演示向BeanFactory中Load常用的BeanFactoryPostProcessor和BeanPostProcessor */
        //1. 创建BeanFactory，因为需要从容器中注册BeanDefinition，因此需要使用到BeanDefinitionRegistry的接口能力
        Object beanFactory = new DefaultListableBeanFactory();
        //2. 使用AnnotationConfigUtils的registerAnnotationConfigProcessors方法，向BeanFactory注入一些常见的注解处理后处理器：
        /**
         * 具体如下：
         * 1) ConfigurationClassPostProcessor -- BeanFactoryPostProcessor，用于解析@Component、@ComponentScan、@Import、@ImportResource、@Bean等注解
         * 2) AutowiredAnnotationBeanPostProcessor -- BeanPostProcessor，用于处理@Autowired、@Value注解
         * 3) CommonAnnotationBeanPostProcessor -- BeanPostProcessor,用于解析JSR-250注解，解析如@Resource、@PostConstruct、@PreDestroy、@Priority等
         * -- 3.1) InitDestroyAnnotationBeanPostProcessor -- BeanPostProcessor，用于在Bean的各个阶段中寻找并调用 @PostConstruct、@PreDestroy 的方法，实际上CommonAnnotationBeanPostProcessor 内部就是集成了 InitDestroyAnnotationBeanPostProcessor
         * 4) EventListenerMethodProcessor -- BeanFactoryPostProcessor，用于解析@EventListener注解
         * 5）DefaultEventListenerFactory -- 不是PostProcessor，但是负责将EventListenerMethodProcessor扫描出来的监听器在容器中创建实例
         * */
        if(beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry){
            AnnotationConfigUtils.registerAnnotationConfigProcessors(beanDefinitionRegistry);
            showBeanDefinitionInBeanFactory(beanDefinitionRegistry);
            //3. 实际上这些BeanFactoryPostProcessor和BeanPostProcessor仅仅是在容器中注册了BeanDefinition而已，还没有生成实例并作用于容器
            // 在使其生效之前，可以在容器中添加配置类ConfigA的BeanDefinition用于演示这些后处理器的作用效果
            AbstractBeanDefinition configABD = BeanDefinitionBuilder
                    .genericBeanDefinition(ConfigClass01.class)
                    .setScope("singleton")
                    .getBeanDefinition();
            beanDefinitionRegistry.registerBeanDefinition("configA",configABD );
        }
        //4. 从容器中拿出BeanFactoryPostProcessor，调用这个BeanFacotryPostProcessor的postProcessorBeanFactory方法
        // 因为涉及到 ListableBeanFactory的getBeansOfType接口方法、以及postProcessorBeanFactory参数要求ConfigurableBeanFactory，所以这里使用ConfigurableListableBeanFactory
        if(beanFactory instanceof ConfigurableListableBeanFactory clBeanFactory){
            clBeanFactory.getBeansOfType(BeanFactoryPostProcessor.class).forEach((name,postprocessor)->{
                postprocessor.postProcessBeanFactory(clBeanFactory);
                log.info(Strings.lenientFormat("BeanFactoryPostProcessor： 【%s】 已装配",postprocessor.getClass().getSimpleName()));
            });
            //5. 从容器中拿出BeanPostProcessor的BeanPostProcessor，调用ConfigurableBeanFactory的addBeanPostProcessor方法
            clBeanFactory.getBeansOfType(BeanPostProcessor.class).forEach((name,postprocessor)->{
                clBeanFactory.addBeanPostProcessor(postprocessor);
                log.info(Strings.lenientFormat("BeanPostProcessor： 【%s】 已加入",postprocessor.getClass().getSimpleName()));
            });
            //6.展示效果，先预实例化容器
//            clBeanFactory.preInstantiateSingletons();
            SampleBean01 sampleBean01 = clBeanFactory.getBean(SampleBean01.class);
            SampleBean02 sampleBean02 = sampleBean01.getSampleBean02();
            System.out.println(sampleBean02);
            showBeanDefinitionInBeanFactory(clBeanFactory);
        }

        /* 2. 演示向ApplicationContext中加载PostProcessor */






    }

    public static void showBeanDefinitionInBeanFactory(Object beanFactory){
        if(beanFactory instanceof BeanDefinitionRegistry beanDefinitionRegistry){
            for (String beanDefinitionName : beanDefinitionRegistry.getBeanDefinitionNames()) {
                log.info(Strings.lenientFormat("-----\n  beanDefinitionName: %s  \n BeanClass: %s \n ------",beanDefinitionName,beanDefinitionRegistry.getBeanDefinition(beanDefinitionName).getBeanClassName()));
            }
        }
    }
}
