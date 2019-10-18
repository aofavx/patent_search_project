package com.blcultra.model;

public class PatentTextDetail {
    private String ptextid;

    private String pid;

    private String pdetail;

    public String getPtextid() {
        return ptextid;
    }

    public void setPtextid(String ptextid) {
        this.ptextid = ptextid == null ? null : ptextid.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

    public String getPdetail() {
        return pdetail;
    }

    public void setPdetail(String pdetail) {
        this.pdetail = pdetail == null ? null : pdetail.trim();
    }
}