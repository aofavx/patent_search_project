package com.blcultra.controller;

import com.blcultra.dto.SearchDto;
import com.blcultra.service.GraphService;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Created by sgy05 on 2019/10/23.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/patent/api/")
public class GraphController {

    @Autowired
    private GraphService graphService;

    /**
     *知识图谱检索接口
     * @param uid
     * @param expression
     * @param searchword
     * @return
     */
    @PostMapping(value = "graph",produces = "application/json;charset=UTF-8")
    public String graphSearch(@RequestData String uid ,
                                         @RequestParam(value="expression",required = true)  String expression,
                                         @RequestParam(value="searchword",required = true)  String searchword){

        String res = graphService.graphSearch(uid,expression,searchword);
        return res;
    }
}
