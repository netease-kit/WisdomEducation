package com.netease.edu.sample.pojo;

/**
 * Created by yuyang04 on 2021/3/15.
 */
public class RoleConfig {
    private Integer limit;
    private Boolean superRole;
    private Boolean sitMember;
    public Integer getLimit() {
        return limit;
    }

    public Boolean getSuperRole() {
        return superRole;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public void setSuperRole(Boolean superRole) {
        this.superRole = superRole;
    }

    public Boolean getSitMember() {
        return sitMember;
    }

    public void setSitMember(Boolean sitMember) {
        this.sitMember = sitMember;
    }
}
