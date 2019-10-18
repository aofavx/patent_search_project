package com.blcultra.controller;

import com.blcultra.dto.SearchDto;
import com.blcultra.service.PatentInfoService;
import com.blcultra.support.RequestData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 专利信息相关服务接口
 * Created by sgy05 on 2019/8/5.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/patent/api/")
public class PatentInfoController extends BaseController {

    private static Logger logger = LoggerFactory.getLogger(PatentInfoController.class);

    @Autowired
    private PatentInfoService patentInfoService;

    /**
     * 首页数据加载接口，包含：专利的各个审核状态的数量以及各状态下专利信息列表
     * @param uid
     * @param pageNow
     * @param pageSize
     * @return
     */
    @GetMapping(value = "home")
    public String index(@RequestData String uid,
                        @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                        @RequestParam (value = "pageSize" ,required =false,defaultValue = "10" ) Integer pageSize){

        logger.info("***************patent home  index  method params  ===>  pageNow:{} ,pagesize:{}",pageNow,pageSize);
        String res = patentInfoService.index(uid,pageNow,pageSize);
        return res;

    }

    /**
     * 查询专利列表信息，在上传的专利列表页面，点击下一页查询时调用
     * @param uid 用户id
     * @param  pstate 查询的专利信息列表是已审核的还是未审核的或者还是全部的：0：未审核   1：已审核   2：全部
     * @param pageNow
     * @param pageSize
     * @return
     */
    @GetMapping(value = "list")
    public String listPatent(@RequestData String uid,
                             @RequestParam(value = "pstate",required = false,defaultValue = "2") String pstate,
                             @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                             @RequestParam (value = "pageSize" ,required =false,defaultValue = "10" ) Integer pageSize){

        String res = patentInfoService.listPatents(uid,pstate,pageNow,pageSize);
        return res;

    }

    /**
     * 根据专利id删除专利信息,可批量删除，多个pid用英文逗号分隔
     * @param pids
     * @return
     */
    @PostMapping(value = "delete",produces = "application/json;charset=UTF-8")
    public String delete(@RequestData String uid ,@RequestParam(value="pids",required = true)  String pids){

        String res = patentInfoService.delete(pids,uid);
        return res;
    }

    /**
     * 查询审核和未审核的专利数量
     * @return
     */
    @GetMapping(value = "count",produces = "application/json;charset=UTF-8")
    public String getCountPatent(){
        log.info("*****************查询已审和待审的专利数。。。");
        String res = patentInfoService.countPatent();
        return res;
    }

    /**
     * 审查接口
     * @param pids
     * @param checktype  1 ：一站式检索路线逻辑    2：自动检索
     * @return
     */
    @PostMapping(value = "check",produces = "application/json;charset=UTF-8")
    public String check(@RequestData String uid,
                        @RequestParam(value="pids",required = true)  String pids,
                        @RequestParam(value="checktype",required = true) String checktype){

        String res = patentInfoService.check(uid,pids,checktype);
        return res;
    }

    /**
     * 检索对比文献  结果列表【用户点击特征词提取页面中的“检索对比文献”按钮，触发调用此接口】
     * @param searchDto  此参数根据底层BCC检索系统所需数据结构进行定义
     * @return
     */
    @PostMapping(value = "docs",produces = "application/json;charset=UTF-8")
    public String searchComparisonPatent(@RequestData String uid ,@RequestBody SearchDto searchDto){

        String res = patentInfoService.searchComparisonPatent(uid,searchDto);
        return res;
    }

    /**
     * 首页---- 专利申请列表  搜索框中输入检索关键词对专利列表进行模糊搜索【需求：按申请号、时间、申请人 ，进行检索】
     * @param uid  用户id
     * @param pstate 审核状态：0：未审核   1：已审核   2：全部（包含未审核和已审核）
     * @param keyword
     * @param pageNow
     * @param pageSize
     * @return
     */
    @GetMapping(value = "search",produces = "application/json;charset=UTF-8")
    public String likeSearchPatents(@RequestData String uid,
                                    @RequestParam(value = "pstate",required = false) String pstate,
                                    @RequestParam(value = "keyword",required = false) String keyword,
                                    @RequestParam(value = "pageNow",required = false,defaultValue = "1") Integer pageNow,
                                    @RequestParam (value = "pageSize" ,required =false,defaultValue = "10" ) Integer pageSize){

        String res = patentInfoService.likeSearchPatents(uid,pstate,keyword,pageNow,pageSize);
        return res;
    }

    /**
     * 浏览要审核的专利正文----【点击特征词提取页面中的“浏览”按钮，触发此接口查询该专利的正文信息】
     * @param pid
     * @return
     */
    @GetMapping(value = "browse",produces = "application/json;charset=UTF-8")
    public String browsePatent(@RequestData String uid ,@RequestParam(value = "pid",required = false) String pid){

        String res = patentInfoService.browsePatent(uid,pid);
        return res;
    }


    /**
     * 查看原文   在检索结果页面中点击全部对比文献列表中的【查看】、收藏列表中的【原文】按钮调用此接口
     * @param uid
     * @param docnum
     * @return
     */
    @GetMapping(value = "view",produces = "application/json;charset=UTF-8")
    public String viewOriginalPatent(@RequestData String uid ,
                                     @RequestParam(value = "docnum",required = false) String docnum){

        String res = patentInfoService.viewOriginalPatent(uid,docnum);
        return res;
    }




}
