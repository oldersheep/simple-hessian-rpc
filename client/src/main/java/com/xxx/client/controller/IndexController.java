package com.xxx.client.controller;

import com.xxx.api.HeroModel;
import com.xxx.api.HeroService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName IndexController
 * @Description 测试Controller
 * @Author l17561
 * @Date 2018/12/13 11:37
 * @Version V1.0
 */
@RestController
public class IndexController {

    @Autowired
    private HeroService heroService;

    @RequestMapping("/")
    public HeroModel index(){
        HeroModel nevermore = heroService.getOneByName("Nevermore");
        System.out.println(nevermore);
        return nevermore;
    }
}
