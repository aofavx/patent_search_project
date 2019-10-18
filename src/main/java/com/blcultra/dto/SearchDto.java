package com.blcultra.dto;

import java.util.List;

/**
 * Created by sgy05 on 2019/10/14.
 */
public class SearchDto {

    private String pid;

    private String category;

    private List<String> corewords;

    private List<String> featurewords;

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
