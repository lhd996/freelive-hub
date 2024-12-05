package com.lhd.entity.enums;

public enum UserStatuseEnum {
    DISABLE(0,"禁用"), ENABLE(1,"启用");

    private Integer status;
    private String desc;

    UserStatuseEnum(Integer status, String desc) {
        this.status = status;
        this.desc = desc;
    }
    public Integer getStatus(){
        return status;
    }
    public String getDesc(){
        return desc;
    }
}
