package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.constants.PatentSearchConstant;
import com.blcultra.dto.ExpressionDto;
import com.blcultra.service.SearchExpressionService;
import com.blcultra.support.JsonModel;
import com.blcultra.util.StringUtil;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/10/22.
 */
@Service(value = "searchExpressionService")
public class SearchExpressionServiceImpl implements SearchExpressionService {


    @Override
    public String getExpression(String uid, ExpressionDto expressionDto) {

        String expression = "";

        if (expressionDto.getSearchtype().equals(PatentSearchConstant.boolsearchtype)){
            //布尔检索表达式生成
            List<String> boolcorewords = expressionDto.getBoolcorewords();
            List<String> boolfeaturewords = expressionDto.getBoolfeaturewords();
            if (CollectionUtils.isEmpty(boolcorewords)&& CollectionUtils.isEmpty(boolfeaturewords)){
                JsonModel resjson = new JsonModel(false, "布尔检索参数校验错误","400",null);
                return JSON.toJSONString(resjson);
            }

            String corejoin = String.join(PatentSearchConstant.expressionunion, boolcorewords);
            String featurejoin = String.join(PatentSearchConstant.expressionunion, boolfeaturewords);
            expression = PatentSearchConstant.expressionprefix+corejoin + PatentSearchConstant.expressionsuffix + PatentSearchConstant.expressionintersection +
                    PatentSearchConstant.expressionprefix + featurejoin + PatentSearchConstant.expressionsuffix;


        }else if (expressionDto.getSearchtype().equals(PatentSearchConstant.structsearchtype)){
            //结构检索表达式生成   (n)的(~){$2=[碗 玻璃杯 底座];print($1)}

            List<String> structwords = expressionDto.getStructwords();
            if (CollectionUtils.isEmpty(structwords)){
                JsonModel resjson = new JsonModel(false, "结构检索参数校验错误","400",null);
                return JSON.toJSONString(resjson);
            }
            String join = String.join(" ", structwords);

            expression = PatentSearchConstant.jiegou_prefix + join + PatentSearchConstant.jiegou_suffix;

        }else if (expressionDto.getSearchtype().equals(PatentSearchConstant.graphsearchtype)){
            //图谱表达式生成  (n)的氨基酸{print($1)}
            String word = expressionDto.getGraphword();
            if (StringUtil.isBlank(word.trim())){
                JsonModel resjson = new JsonModel(false, "图谱检索参数校验错误","400",null);
                return JSON.toJSONString(resjson);
            }
            expression = PatentSearchConstant.graphexpression_prefix + expressionDto.getGraphword() + PatentSearchConstant.graphexpression_suffix;

        }
        Map<String,String> resultmap = new HashMap<>();
        resultmap.put("expression",expression);
        JsonModel resjson = new JsonModel(true, "生成表达式成功","200",resultmap);
        return JSON.toJSONString(resjson);
    }
}
