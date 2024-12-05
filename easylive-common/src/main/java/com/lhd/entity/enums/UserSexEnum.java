package com.lhd.entity.enums;

public enum UserSexEnum {
    WOMAN(0,"女"),MAN(1,"男"),SECRECY(2,"保密");

    private Integer type;
    private String desc;
    UserSexEnum(Integer type, String desc) {
        this.type = type;
        this.desc = desc;
    }
    public Integer getType() {
        return type;
    }
    public String getDesc() {
        return desc;
    }
}
