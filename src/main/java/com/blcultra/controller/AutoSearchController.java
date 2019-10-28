package com.blcultra.controller;

import com.blcultra.service.AutoSearchService;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by sgy05 on 2019/10/23.
 */
@RestController
@RequestMapping(value = "/patent/api/")
public class AutoSearchController {


    @Autowired
    private AutoSearchService autoSearchService;

    /**
     * 自动检索API接口服务
     * @param uid 用户id
     * @param pid 待审专利记录信息id
     * @param checktype 自动检索中那种检索类型方式：2000：默认走：bm25--->bert   top500内  200个建议
     *                                         2001、ES、bert
                                               2002、只走bm25
     * @return
     */
    @PostMapping(value = "auto_check",produces = "application/json;charset=UTF-8")
    public String autoCheck(@RequestData String uid,
                        @RequestParam(value="pid",required = true)  String pid,
                        @RequestParam(value="checktype",required = true,defaultValue = "2000") String checktype){

        String res = autoSearchService.autoCheck(uid,pid,checktype);
        return res;
    }



}
