package com.blcultra.model;

public class PatentCoreWord {
    private String cid;

    private String pid;

    private String coreword;

    private String cgrade;

    private String uid;

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid == null ? null : cid.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

    public String getCoreword() {
        return coreword;
    }

    public void setCoreword(String coreword) {
        this.coreword = coreword == null ? null : coreword.trim();
    }

    public String getCgrade() {
        return cgrade;
    }

    public void setCgrade(String cgrade) {
        this.cgrade = cgrade == null ? null : cgrade.trim();
    }
}