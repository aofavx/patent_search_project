package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.dao.PatentCollectInfoMapper;
import com.blcultra.dao.PatentInfoMapper;
import com.blcultra.dao.PatentSearchHistoryMapper;
import com.blcultra.model.PatentCollectInfo;
import com.blcultra.model.PatentInfoWithBLOBs;
import com.blcultra.service.PatentCollectService;
import com.blcultra.support.JsonModel;
import com.blcultra.util.DateFormatUtil;
import com.blcultra.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/8/28.
 */
@Service(value = "patentCollectService")
public class PatentCollectServiceImpl implements PatentCollectService {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private PatentInfoMapper patentInfoMapper;
    @Autowired
    private PatentCollectInfoMapper patentCollectInfoMapper;
    @Autowired
    private PatentSearchHistoryMapper patentSearchHistoryMapper;

    /**
     *点击【收藏】按钮收藏检索记录
     * @param uid
     * @param patentCollectInfo
     * @return
     */
    @Override
    public String collect(String uid, PatentCollectInfo patentCollectInfo) {

        try {
            //保存收藏记录
            Map<String,Object> pmap = new HashMap<>();
            pmap.put("collector",uid);
            pmap.put("pdocnum",patentCollectInfo.getPdocnum());
            pmap.put("pid",patentCollectInfo.getPid());
            PatentCollectInfo pcinfo = patentCollectInfoMapper.selectCollect(pmap);
            boolean flag = false;
            if (null == pcinfo){
                patentCollectInfo.setCollectid(StringUtil.getUUID());
                patentCollectInfo.setCollector(uid);
                patentCollectInfo.setCollecttime(DateFormatUtil.DateFormat());
                patentCollectInfo.setUpdatetime(DateFormatUtil.DateFormat());
                int m = patentCollectInfoMapper.insertSelective(patentCollectInfo);
                flag = true;
            }

            //收藏成功后更新检索历史记录表中的检索历史记录
            //获取被审核专利的id
            String pid = patentCollectInfo.getPid();
            String pdocnum = patentCollectInfo.getPdocnum();
            String time = DateFormatUtil.DateFormat();

            Map<String,Object> param = new HashMap<>();
            param.put("pid",pid);
            param.put("pdocnum",pdocnum);
            param.put("updatetime",time);

            //更新检索历史记录的docnum和时间
            int n = patentSearchHistoryMapper.updateRecord(param);
            if (flag){
                JsonModel json = new JsonModel(true, "收藏成功","200",patentCollectInfo);
                return JSON.toJSONString(json);
            }else {
                JsonModel json = new JsonModel(true, "收藏成功","200",pcinfo);
                return JSON.toJSONString(json);
            }
        } catch (Exception e) {
            logger.error("错误信息===>",e);
        }
        JsonModel json = new JsonModel(false, "收藏失败","400",null);
        return JSON.toJSONString(json);
    }

    /**
     * 删除收藏记录
     * @param collectid
     * @return
     */
    @Override
    public String delete(String collectid) {

        try {
            if (StringUtils.isEmpty(collectid)){
                JsonModel json = new JsonModel(false, "参数错误","400",null);
                return JSON.toJSONString(json);
            }
            int m = patentCollectInfoMapper.deleteByPrimaryKey(collectid);
            JsonModel json = new JsonModel(true, "删除成功","200",null);
            return JSON.toJSONString(json);

        } catch (Exception e) {
            logger.error("*********delete collect info  ERROR:",e);
        }
        JsonModel json = new JsonModel(false, "删除失败","400",null);
        return JSON.toJSONString(json);
    }

    /**
     * 点击收藏列表中某一条记录的【检索详情】按钮，查询收藏此记录前的检索记录信息
     * @param uid
     * @param pid
     * @param pdocnum
     * @return
     */
    @Override
    public String detail(String uid, String pid, String pdocnum) {

        Map<String,Object> param = new HashMap<>();
        param.put("searcher",uid);
        param.put("pid",pid);
        param.put("pdocnum",pdocnum);

        //获取检索历史记录列表，按检索触发的时间排序
        List<Map<String, Object>> historyList = patentSearchHistoryMapper.getHsitoryList(param);
        List<JSONObject> hislist = new ArrayList<>();
        for (Map<String, Object> map : historyList) {
            Object searchparam = map.get("searchparam");
            Object searchtime = map.get("searchtime");
            JSONObject jsonObject = JSONObject.parseObject(searchparam.toString());
            jsonObject.put("searchtime",searchtime);
            hislist.add(jsonObject);
        }


        Map<String,Object> map = new HashMap<>();
        map.put("collector",uid);
        map.put("pdocnum",pdocnum);
        map.put("pid",pid);

        PatentInfoWithBLOBs patentInfoWithBLOBs = patentInfoMapper.selectByPrimaryKey(pid);
        String appid = patentInfoWithBLOBs.getAppid();
        //获取收藏记录的时间等参数
        Map<String, Object> collectInfo = patentCollectInfoMapper.getCollectInfo(map);
        Map<String,Object> resultmap = new HashMap<>();
        resultmap.put("historyinfos",hislist);
        resultmap.put("collectinfo",collectInfo);
        resultmap.put("appid",appid);
        resultmap.put("pdocnum",pdocnum);

        JsonModel json = new JsonModel(true, "检索详情成功","200",resultmap);
        return JSON.toJSONString(json);

    }


}
