package com.blcultra.model;

public class PatentSearchHistory {
    private String phisid;

    private String pid;

    private String searchparam;

    private String searchtime;

    private String searcher;

    private String pdocnum;

    private String updatetime;

    public String getPhisid() {
        return phisid;
    }

    public void setPhisid(String phisid) {
        this.phisid = phisid == null ? null : phisid.trim();
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid == null ? null : pid.trim();
    }

    public String getSearchparam() {
        return searchparam;
    }

    public void setSearchparam(String searchparam) {
        this.searchparam = searchparam == null ? null : searchparam.trim();
    }

    public String getSearchtime() {
        return searchtime;
    }

    public void setSearchtime(String searchtime) {
        this.searchtime = searchtime == null ? null : searchtime.trim();
    }

    public String getSearcher() {
        return searcher;
    }

    public void setSearcher(String searcher) {
        this.searcher = searcher == null ? null : searcher.trim();
    }

    public String getPdocnum() {
        return pdocnum;
    }

    public void setPdocnum(String pdocnum) {
        this.pdocnum = pdocnum == null ? null : pdocnum.trim();
    }

    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime == null ? null : updatetime.trim();
    }
}