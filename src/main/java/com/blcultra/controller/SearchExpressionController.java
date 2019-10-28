package com.blcultra.controller;

import com.blcultra.dto.ExpressionDto;
import com.blcultra.dto.SearchDto;
import com.blcultra.service.SearchExpressionService;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 检索表达式生成服务接口API
 * Created by sgy05 on 2019/10/22.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/patent/api/")
public class SearchExpressionController {

    @Autowired
    private SearchExpressionService searchExpressionService;


    @PostMapping(value = "expression",produces = "application/json;charset=UTF-8")
    public String getExpression(@RequestData String uid , @RequestBody ExpressionDto expressionDto){

        String res = searchExpressionService.getExpression(uid,expressionDto);
        return res;
    }


}
