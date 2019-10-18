package com.blcultra.service;

import com.blcultra.dto.SearchDto;
import com.blcultra.model.PatentCollectInfo;

import java.util.Map;

/**
 * Created by sgy05 on 2019/8/5.
 */
public interface PatentInfoService {

    String index(String uid, Integer pageNow, Integer pageSize);

    String listPatents(String uid,  String pstate,Integer pageNow, Integer pageSize);

    String delete(String pids,String uid);

    String countPatent();

    String check(String uid,String pids,String checktype);

    String searchComparisonPatent(String uid ,SearchDto searchDto);

    String likeSearchPatents(String uid, String pstate,String keyword,Integer pageNow,Integer pageSize);

    String browsePatent(String uid ,String pid);

    String viewOriginalPatent(String uid ,String docnum);


}
