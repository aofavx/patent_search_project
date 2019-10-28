package com.blcultra.dao;

import com.blcultra.model.PatentCollectInfo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PatentCollectInfoMapper {

    PatentCollectInfo selectCollect(Map<String,Object> map);
    Map<String,Object> getCollectInfo(Map<String,Object> map);

    List<Map<String,Object>> selectCollectInfos(Map<String,Object> map);

    int deleteByPrimaryKey(String collectid);

    int insert(PatentCollectInfo record);

    int insertSelective(PatentCollectInfo record);

    PatentCollectInfo selectByPrimaryKey(String collectid);

    int updateByPrimaryKeySelective(PatentCollectInfo record);

    int updateByPrimaryKey(PatentCollectInfo record);
}