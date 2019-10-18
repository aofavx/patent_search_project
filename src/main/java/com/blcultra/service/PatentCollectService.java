package com.blcultra.service;

import com.blcultra.model.PatentCollectInfo;

/**
 * Created by sgy05 on 2019/8/28.
 */
public interface PatentCollectService {

    String collect(String uid,PatentCollectInfo patentCollectInfo);

    String delete(String collectid);

    String detail(String uid,String pid,String pdocnum);
}
