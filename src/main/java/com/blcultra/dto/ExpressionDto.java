package com.blcultra.dto;

import java.util.List;

/**
 * Created by sgy05 on 2019/10/22.
 */
public class ExpressionDto {

    private String searchtype;//检索类型：1001：布尔检索表达式生成    1002：结构检索表达式生成   1003：图谱检索表达式生成

    private String graphword;//图谱输入词

    private List<String> boolcorewords;//布尔检索核心词

    private List<String> boolfeaturewords;//布尔检索特征词

    private List<String> structwords;//结构检索条件词组

    public String getSearchtype() {
        return searchtype;
    }

    public void setSearchtype(String searchtype) {
        this.searchtype = searchtype;
    }

    public String getGraphword() {
        return graphword;
    }

    public void setGraphword(String graphword) {
        this.graphword = graphword;
    }

    public List<String> getBoolcorewords() {
        return boolcorewords;
    }

    public void setBoolcorewords(List<String> boolcorewords) {
        this.boolcorewords = boolcorewords;
    }

    public List<String> getBoolfeaturewords() {
        return boolfeaturewords;
    }

    public void setBoolfeaturewords(List<String> boolfeaturewords) {
        this.boolfeaturewords = boolfeaturewords;
    }

    public List<String> getStructwords() {
        return structwords;
    }

    public void setStructwords(List<String> structwords) {
        this.structwords = structwords;
    }
}
