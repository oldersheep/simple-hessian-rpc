package com.xxx.service.processor;

import com.xxx.service.annotation.HessianService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.remoting.caucho.HessianServiceExporter;
//import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.stereotype.Component;

/**
 * @ClassName HessianServiceScanner
 * @Description TODO
 * @Author l17561
 * @Date 2018/12/13 11:26
 * @Version V1.0
 */
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
