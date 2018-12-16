package com.xxx.client.controller;

import com.xxx.api.HeroModel;
import com.xxx.api.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName IndexController
 * @Description
 * @Author l17561
 * @Date 2018/12/13 11:37
 * @Version V1.0
 */
@RestController
public class IndexController {

    @Autowired
    private HeroService heroService;

    @Value("${server.url}")
    private String url;

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
}
