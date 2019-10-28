package com.blcultra.model;

public class PatentFeatureWord {
    private String fid;

    private String pid;

    private String corewords;

    private String featurewords;

    private String extendcoreword;

    private String extendfeatureword;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid == null ? null : fid.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

    public String getCorewords() {
        return corewords;
    }

    public void setCorewords(String corewords) {
        this.corewords = corewords == null ? null : corewords.trim();
    }

    public String getFeaturewords() {
        return featurewords;
    }

    public void setFeaturewords(String featurewords) {
        this.featurewords = featurewords == null ? null : featurewords.trim();
    }

    public String getExtendcoreword() {
        return extendcoreword;
    }

    public void setExtendcoreword(String extendcoreword) {
        this.extendcoreword = extendcoreword == null ? null : extendcoreword.trim();
    }

    public String getExtendfeatureword() {
        return extendfeatureword;
    }

    public void setExtendfeatureword(String extendfeatureword) {
        this.extendfeatureword = extendfeatureword == null ? null : extendfeatureword.trim();
    }
}