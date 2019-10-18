package com.blcultra.dao;

import com.blcultra.model.PatentInfo;
import com.blcultra.model.PatentInfoWithBLOBs;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

@Mapper
public interface PatentInfoMapper {

    Map<String,Object> browse(String pid);

//    int batchInsertPatents(@Param(value = "list") List<Map<String,Object>> list);

    List<Map<String,Object>> likeSearchPatents(Map<String, Object> param);

    List<Map<String,Object>> getPList(Map<String, Object> param);

    List<Map<String,Object>> getPatentList(Map<String, Object> map);

    int batchUpdate(Map<String, Object> param);

    int count(Map<String, Object> map);

    int countChecked();

    int countUnchecked();

    

    int deleteByPrimaryKey(String pid);

    int insert(PatentInfoWithBLOBs record);

    int insertSelective(PatentInfoWithBLOBs record);

    PatentInfoWithBLOBs selectByPrimaryKey(String pid);

    int updateByPrimaryKeySelective(PatentInfoWithBLOBs record);

    int updateByPrimaryKeyWithBLOBs(PatentInfoWithBLOBs record);

    int updateByPrimaryKey(PatentInfo record);
}