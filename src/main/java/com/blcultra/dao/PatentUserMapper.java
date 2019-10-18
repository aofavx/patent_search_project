package com.blcultra.dao;

import com.blcultra.model.PatentUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface PatentUserMapper {
    int insert(PatentUser record);

    int insertSelective(PatentUser record);


    PatentUser selectUserByUserName(String username);
}