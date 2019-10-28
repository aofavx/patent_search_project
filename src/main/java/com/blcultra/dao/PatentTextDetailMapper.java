package com.blcultra.dao;

import com.blcultra.model.PatentTextDetail;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PatentTextDetailMapper {

    Map<String,String> selectByPid(Map<String,Object> param);

    int deleteByPrimaryKey(String ptextid);

    int insert(PatentTextDetail record);

    int insertSelective(PatentTextDetail record);

    PatentTextDetail selectByPrimaryKey(String ptextid);

    int updateByPrimaryKeySelective(PatentTextDetail record);

    int updateByPrimaryKeyWithBLOBs(PatentTextDetail record);

    int updateByPrimaryKey(PatentTextDetail record);
}