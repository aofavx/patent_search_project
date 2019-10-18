package com.blcultra.service;

import com.blcultra.dto.CoreWordDto;
import com.blcultra.dto.FeatureWordDto;
import com.blcultra.dto.TermWordDto;

/**
 * Created by sgy05 on 2019/10/16.
 */
public interface FeatureWordService {

    String deleteCoreWord(String uid,String cid);

    String addCoreWord(String uid,CoreWordDto coreWordDto);

    String deleteTermWord(String uid,String tid);

    String addTermWord(String uid,TermWordDto termWordDto);

    String updateFeatureword(String uid ,FeatureWordDto featureWordDto);
}
