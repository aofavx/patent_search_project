package com.blcultra.dao;

import com.blcultra.model.PatentSearchHistory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PatentSearchHistoryMapper {

    List<Map<String,Object>>  getHsitoryList(Map<String,Object> param);

    int updateRecord(Map<String,Object> param);

    int deleteByPrimaryKey(String phisid);

    int insert(PatentSearchHistory record);

    int insertSelective(PatentSearchHistory record);

    PatentSearchHistory selectByPrimaryKey(String phisid);

    int updateByPrimaryKeySelective(PatentSearchHistory record);

    int updateByPrimaryKey(PatentSearchHistory record);
}