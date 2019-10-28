package com.blcultra.dao;

import com.blcultra.model.PatentTermWord;
import org.apache.ibatis.annotations.Mapper;
import org.omg.CORBA.OBJ_ADAPTER;

import java.util.List;
import java.util.Map;

@Mapper
public interface PatentTermWordMapper {

    Map<String,Object> selectTermWord(Map<String,Object> param);

    int batchInsertTerms(List<PatentTermWord> termlist);

    List<PatentTermWord> selectTermsByPid(String patentid);

    int deleteByPrimaryKey(String tid);

    int insert(PatentTermWord record);

    int insertSelective(PatentTermWord record);

    PatentTermWord selectByPrimaryKey(String tid);

    int updateByPrimaryKeySelective(PatentTermWord record);

    int updateByPrimaryKey(PatentTermWord record);
}