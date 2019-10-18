package com.blcultra.controller;

import com.blcultra.model.PatentCollectInfo;
import com.blcultra.service.PatentCollectService;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sgy05 on 2019/8/28.
 */
@RestController
@RequestMapping(value = "/patent/api/")
public class PatentCollectController extends BaseController{


    @Autowired
    private PatentCollectService patentCollectService;

    /**
     * 在检索结果页面中，检索结果列表中点击【收藏】按钮，收藏对比文献信息
     * @param uid
     * @param patentCollectInfo
     * @return
     */
    @PostMapping(value = "collect/add",produces = "application/json;charset=UTF-8")
    public String collect(@RequestData String uid , @RequestBody PatentCollectInfo patentCollectInfo){

        String res = patentCollectService.collect(uid,patentCollectInfo);
        return res;
    }

    /**
     * 删除收藏记录服务
     * @param collectid  收藏记录id
     * @return
     */
    @PostMapping(value = "collect/delete",produces = "application/json;charset=UTF-8")
    public String delete(@RequestParam(value = "collectid",required = true) String collectid){
        log.info("DELETE COLLECT INFO  PARAM,collectid：{}",collectid);

        String res = patentCollectService.delete(collectid);
        return res;
    }

    /**
     * 点击收藏列表中某一条记录的【检索详情】按钮，查询收藏此记录前的检索记录信息
     * @param uid
     * @return
     */
    @PostMapping(value = "collect/search_detail",produces = "application/json;charset=UTF-8")
    public String detail(@RequestData String uid,
                         @RequestParam(value = "pid",required = true) String pid ,
                         @RequestParam(value = "pdocnum",required = true) String pdocnum){

        String res = patentCollectService.detail(uid,pid,pdocnum);
        return res;
    }

}
