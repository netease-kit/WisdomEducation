package com.netease.edu.sample.parameter;

import org.apache.commons.lang3.BooleanUtils;

public class UserCreateParam {
    private String userToken;
    private String imToken;
    private Boolean assertNotExist;
    private Boolean updateOnConflict;
    public UserCreateParam() {
    }

    public String getUserToken() {
        return userToken;
    }

    public void setUserToken(String userToken) {
        this.userToken = userToken;
    }

    public String getImToken() {
        return imToken;
    }

    public void setImToken(String imToken) {
        this.imToken = imToken;
    }

    public Boolean getAssertNotExist() {
        return assertNotExist;
    }

    public void setAssertNotExist(Boolean assertNotExist) {
        this.assertNotExist = assertNotExist;
    }

    public Boolean getUpdateOnConflict() {
        return updateOnConflict;
    }

    public void setUpdateOnConflict(Boolean updateOnConflict) {
        this.updateOnConflict = updateOnConflict;
    }

    public boolean assertNotExist(){
        return BooleanUtils.isTrue(assertNotExist);
    }

    public boolean updateOnConflict(){
        return BooleanUtils.isTrue(updateOnConflict);
    }
}
