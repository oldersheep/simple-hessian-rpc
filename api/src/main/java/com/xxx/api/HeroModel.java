package com.xxx.api;

import java.io.Serializable;

/**
 * @ClassName HeroModel
 * @Description 英雄模型
 * @Author l17561
 * @Date 2018/12/13 11:12
 * @Version V1.0
 */
public class HeroModel implements Serializable {

    private String name;
    private String type;
    private String position;
    private String nickname;

    public HeroModel() {
    }

    public HeroModel(String name, String type, String position, String nickname) {
        this.name = name;
        this.type = type;
        this.position = position;
        this.nickname = nickname;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String toString() {
        return "{" +
                "\"name\":\"" + name + "\"," +
                "\"type\":\"" + type + "\"," +
                "\"position\":\"" + position + "\"," +
                "\"nickname\":\"" + nickname + "\"" +
                '}';
    }
}
