package com.xxx.client.config;

import com.xxx.api.HeroService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;

/**
 * @ClassName AheadConfiguration
 * @Description 提前加载的Configure，如何实现呢？
 * @Author l17561
 * @Date 2018/12/17 14:58
 * @Version V1.0
 */
@Configuration
public class AheadConfiguration {

    @Value("${server.url}")
    private String url;

    @Bean
    public HessianProxyFactoryBean helloClient() {
        HessianProxyFactoryBean factory = new HessianProxyFactoryBean();
        factory.setServiceUrl(url + "/heroService");
        factory.setServiceInterface(HeroService.class);
        return factory;
    }

}
