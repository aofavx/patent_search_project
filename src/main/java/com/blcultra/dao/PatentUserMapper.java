package com.blcultra.dao;

import com.blcultra.model.PatentUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.Map;

@Mapper
public interface PatentUserMapper {

    Map<String,Object> selectByUserid(String userid);

    int insert(PatentUser record);

    int insertSelective(PatentUser record);


    PatentUser selectUserByUserName(String username);
}