package com.xxx.service.bo;

import com.xxx.api.HeroModel;
import com.xxx.api.HeroService;
import com.xxx.service.annotation.HessianService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName HeroServiceImpl
 * @Description 接口实现类
 * @Author l17561
 * @Date 2018/12/13 11:28
 * @Version V1.0
 */
@HessianService(value = "heroService")
public class HeroServiceImpl implements HeroService {

    @Override
    public HeroModel getOneByName(String name) {

        Map<String, HeroModel> heroMap = new HashMap<>();
        HeroModel nevermore = new HeroModel("Nevermore", "敏捷英雄", "中单", "sf");
        HeroModel qop = new HeroModel("Queen Of Pain", "智力英雄", "中单", "qop");
        HeroModel tiny = new HeroModel("Tiny", "力量英雄", "中单/劣单/C位", "小小");

        heroMap.put("Nevermore", nevermore);
        heroMap.put("Queen Of Pain", qop);
        heroMap.put("Tiny", tiny);


        HeroModel heroModel = heroMap.get(name);

        return heroModel;
    }
}
