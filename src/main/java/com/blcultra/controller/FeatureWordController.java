package com.blcultra.controller;

import com.blcultra.dto.CoreWordDto;
import com.blcultra.dto.FeatureWordDto;
import com.blcultra.dto.TermWordDto;
import com.blcultra.service.FeatureWordService;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sgy05 on 2019/10/16.
 */
@RestController
@RequestMapping(value = "/patent/api/")
@CrossOrigin
public class FeatureWordController extends BaseController {


    @Autowired
    private FeatureWordService featureWordService;

    @PostMapping(value = "deleteCoreWord",produces = "application/json;charset=UTF-8")
    public String deleteCoreWord(@RequestData String uid,
                                      @RequestParam String cid){
        String res = featureWordService.deleteCoreWord(uid,cid);
        return res;
    }


    @PostMapping(value = "addCoreWord",produces = "application/json;charset=UTF-8")
    public String addCoreWord(@RequestData String uid,
                                 @RequestBody CoreWordDto coreWordDto){
        String res = featureWordService.addCoreWord(uid,coreWordDto);
        return res;
    }


    @PostMapping(value = "deleteTermWord",produces = "application/json;charset=UTF-8")
    public String deleteTermWord(@RequestData String uid,
                                 @RequestParam String tid){
        String res = featureWordService.deleteTermWord(uid,tid);
        return res;
    }


    @PostMapping(value = "addTermWord",produces = "application/json;charset=UTF-8")
    public String addTermWord(@RequestData String uid,
                              @RequestBody TermWordDto termWordDto){
        String res = featureWordService.addTermWord(uid,termWordDto);
        return res;
    }
}
