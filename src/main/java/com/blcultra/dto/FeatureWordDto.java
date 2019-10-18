package com.blcultra.dto;

import java.util.List;

/**
 * Created by sgy05 on 2019/10/16.
 */
public class FeatureWordDto {

    private String fid;

    private List<String> corewords;

    private List<String> featurewords;

    public String getFid() {
        return fid;
    }

    public void setFid(String fid) {
        this.fid = fid;
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
