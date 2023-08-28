package com.nftheater.api.constant;

public enum RoleEnum {

    SUPER_ADMIN("SUPER_ADMIN", "Super Administrator", "แอดมินระดับสูงสุด"),
    NETFLIX_AUTHOR("NETFLIX_AUTHOR", "Netflix Authorizer", "หัวหน้าแอดมิน Netflix"),
    NETFLIX_ADMIN("NETFLIX_ADMIN", "Netflix Administrator", "แอดมิน Netflix"),
    YOUTUBE_AUTHOR("YOUTUBE_AUTHOR", "Youtube Authorizer", "หัวหน้าแอดมิน Youtube"),
    YOUTUBE_ADMIN("YOUTUBE_ADMIN", "Youtube Administrator", "แอดมิน Youtube");

    private String roleCode;
    private String roleNameTh;
    private String roleNameEn;

    RoleEnum(String roleCode, String roleNameEn, String roleNameTh){
        this.roleCode = roleCode;
        this.roleNameTh = roleNameTh;
        this.roleNameEn = roleNameEn;
    }

    public String getRoleCode() {
        return roleCode;
    }

    public String getRoleNameTh() {
        return roleNameTh;
    }

    public String getRoleNameEn() {
        return roleNameEn;
    }
}
