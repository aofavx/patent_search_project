package com.blcultra.dao;

import com.blcultra.model.PatentFeatureWord;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PatentFeatureWordMapper {

    Map<String,Object> selectByPid(String patentid);

    int deleteByPrimaryKey(String fid);

    int insert(PatentFeatureWord record);

    int insertSelective(PatentFeatureWord record);

    PatentFeatureWord selectByPrimaryKey(String fid);

    int updateByPrimaryKeySelective(PatentFeatureWord record);

    int updateByPrimaryKey(PatentFeatureWord record);
}