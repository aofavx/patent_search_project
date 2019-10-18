package com.blcultra.model;

public class PatentCollectInfo {
    private String collectid;

    private String ptype;

    private String pdocnum;

    private String pdate;

    private String pipc;

    private String prate;

    private String pid;

    private String collector;

    private String ptermwords;

    private String collectstate;

    private String collecttime;

    private String updatetime;

    public String getCollectid() {
        return collectid;
    }

    public void setCollectid(String collectid) {
        this.collectid = collectid == null ? null : collectid.trim();
    }

    public String getPtype() {
        return ptype;
    }

    public void setPtype(String ptype) {
        this.ptype = ptype == null ? null : ptype.trim();
    }

    public String getPdocnum() {
        return pdocnum;
    }

    public void setPdocnum(String pdocnum) {
        this.pdocnum = pdocnum == null ? null : pdocnum.trim();
    }

    public String getPdate() {
        return pdate;
    }

    public void setPdate(String pdate) {
        this.pdate = pdate == null ? null : pdate.trim();
    }

    public String getPipc() {
        return pipc;
    }

    public void setPipc(String pipc) {
        this.pipc = pipc == null ? null : pipc.trim();
    }

    public String getPrate() {
        return prate;
    }

    public void setPrate(String prate) {
        this.prate = prate == null ? null : prate.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

    public String getCollector() {
        return collector;
    }

    public void setCollector(String collector) {
        this.collector = collector == null ? null : collector.trim();
    }

    public String getPtermwords() {
        return ptermwords;
    }

    public void setPtermwords(String ptermwords) {
        this.ptermwords = ptermwords == null ? null : ptermwords.trim();
    }

    public String getCollectstate() {
        return collectstate;
    }

    public void setCollectstate(String collectstate) {
        this.collectstate = collectstate;
    }

    public String getCollecttime() {
        return collecttime;
    }

    public void setCollecttime(String collecttime) {
        this.collecttime = collecttime == null ? null : collecttime.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }
}