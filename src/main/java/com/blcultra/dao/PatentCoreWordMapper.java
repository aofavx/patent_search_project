package com.blcultra.dao;

import com.blcultra.model.PatentCoreWord;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

@Mapper
public interface PatentCoreWordMapper {

    Map<String,Object> selectCoreWord(Map<String,Object> param);

    int batchInsert(List<PatentCoreWord> corelist);

    List<PatentCoreWord> selectByPid(String patentid);

    int deleteByPrimaryKey(String cid);

    int insert(PatentCoreWord record);

    int insertSelective(PatentCoreWord record);

    PatentCoreWord selectByPrimaryKey(String cid);

    int updateByPrimaryKeySelective(PatentCoreWord record);

    int updateByPrimaryKey(PatentCoreWord record);
}