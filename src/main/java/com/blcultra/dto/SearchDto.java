package com.blcultra.dto;

import java.util.List;

/**
 * Created by sgy05 on 2019/10/14.
 */
public class SearchDto {

    private String isFirst;//是否是初始检索，1：是第一次检索   0：二次检索
    private String secondsearchpattern;//二次检索时选择的检索模式：0：新增（保留）  1：排除
    private String secondsearchword;//二次检索框中输入的词

    private String searchtype;//检索类型：布尔检索   结构检索   基础检索

    private String pid;//待审核专利id

    private String category;//IPC分类

    private List<String> corewords;

    private List<String> featurewords;

    private List<String> localtions;//检索位置
    private String expression;//检索表达式
    private List<String> ipcs;//IPC分类

    public String getIsFirst() {
        return isFirst;
    }

    public void setIsFirst(String isFirst) {
        this.isFirst = isFirst;
    }

    public String getSecondsearchpattern() {
        return secondsearchpattern;
    }

    public void setSecondsearchpattern(String secondsearchpattern) {
        this.secondsearchpattern = secondsearchpattern;
    }

    public String getSecondsearchword() {
        return secondsearchword;
    }

    public void setSecondsearchword(String secondsearchword) {
        this.secondsearchword = secondsearchword;
    }

    public List<String> getLocaltions() {
        return localtions;
    }

    public void setLocaltions(List<String> localtions) {
        this.localtions = localtions;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    public List<String> getIpcs() {
        return ipcs;
    }

    public void setIpcs(List<String> ipcs) {
        this.ipcs = ipcs;
    }

    public String getSearchtype() {
        return searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public List<String> getCorewords() {
        return corewords;
    }

    public void setCorewords(List<String> corewords) {
        this.corewords = corewords;
    }

    public List<String> getFeaturewords() {
        return featurewords;
    }

    public void setFeaturewords(List<String> featurewords) {
        this.featurewords = featurewords;
    }
}
