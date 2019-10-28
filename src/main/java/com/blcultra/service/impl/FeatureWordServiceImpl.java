package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.blcultra.dao.PatentCoreWordMapper;
import com.blcultra.dao.PatentFeatureWordMapper;
import com.blcultra.dao.PatentTermWordMapper;
import com.blcultra.dto.CoreWordDto;
import com.blcultra.dto.FeatureWordDto;
import com.blcultra.dto.TermWordDto;
import com.blcultra.model.PatentCoreWord;
import com.blcultra.model.PatentFeatureWord;
import com.blcultra.model.PatentTermWord;
import com.blcultra.service.FeatureWordService;
import com.blcultra.support.JsonModel;
import com.blcultra.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by sgy05 on 2019/10/16.
 */
@Service(value = "featureWordService")
public class FeatureWordServiceImpl implements FeatureWordService {
    private static final Logger logger = LoggerFactory.getLogger(FeatureWordServiceImpl.class);
//    @Autowired
//    private PatentFeatureWordMapper patentFeatureWordMapper;
    @Autowired
    private PatentCoreWordMapper patentCoreWordMapper;
    @Autowired
    private PatentTermWordMapper patentTermWordMapper;



    @Override
    public String deleteCoreWord(String uid, String cid) {

        try {
            int m = patentCoreWordMapper.deleteByPrimaryKey(cid);
            JsonModel resjson = new JsonModel(true, "删除核心词数据成功","200",null);
            return JSON.toJSONString(resjson);
        } catch (Exception e) {
            logger.error("delete core word method error :"+e);
        }
        JsonModel resjson = new JsonModel(false, "删除核心词数据失败","400",null);
        return JSON.toJSONString(resjson);
    }

    @Override
    public String addCoreWord(String uid, CoreWordDto coreWordDto) {

        try {
            Map<String,Object> param = new HashMap<>();
            param.put("pid",coreWordDto.getPid());
            param.put("coreword",coreWordDto.getCoreword());
            param.put("uid",uid);

            Map<String, Object> map = patentCoreWordMapper.selectCoreWord(param);
            if (null != map && map.size()>0){
                JsonModel resjson = new JsonModel(false, "词核心词已存在","400",null);
                return JSON.toJSONString(resjson);
            }

            PatentCoreWord patentCoreWord = new PatentCoreWord();
            patentCoreWord.setCid(StringUtil.getUUID());
            patentCoreWord.setPid(coreWordDto.getPid());
            patentCoreWord.setCoreword(coreWordDto.getCoreword().trim());
            patentCoreWord.setCgrade("0");
            patentCoreWord.setUid(uid);
            int m = patentCoreWordMapper.insertSelective(patentCoreWord);


            JsonModel resjson = new JsonModel(true, "添加成功","200",patentCoreWord);
            return JSON.toJSONString(resjson);
        } catch (Exception e) {
            logger.error("add core word method:"+e);
        }
        JsonModel resjson = new JsonModel(false, "添加词核心词失败","400",null);
        return JSON.toJSONString(resjson);
    }

    @Override
    public String deleteTermWord(String uid, String tid) {

        try {
            int m = patentTermWordMapper.deleteByPrimaryKey(tid);
            JsonModel resjson = new JsonModel(true, "删除特征词数据成功","200",null);
            return JSON.toJSONString(resjson);
        } catch (Exception e) {
            logger.error("delete term word method error :"+e);
        }
        JsonModel resjson = new JsonModel(false, "删除特征词数据失败","400",null);
        return JSON.toJSONString(resjson);
    }

    @Override
    public String addTermWord(String uid, TermWordDto termWordDto) {
        try {
            Map<String,Object> param = new HashMap<>();
            param.put("pid",termWordDto.getPid());
            param.put("term",termWordDto.getTerm().trim());
            param.put("uid",uid);

            Map<String, Object> map = patentTermWordMapper.selectTermWord(param);
            if (null != map && map.size()>0){
                JsonModel resjson = new JsonModel(false, "词特征词已存在","400",null);
                return JSON.toJSONString(resjson);
            }

            PatentTermWord patentTermWord = new PatentTermWord();
            patentTermWord.setTid(StringUtil.getUUID());
            patentTermWord.setPid(termWordDto.getPid());
            patentTermWord.setTerm(termWordDto.getTerm().trim());
            patentTermWord.setUid(uid);
            int m = patentTermWordMapper.insertSelective(patentTermWord);
            JsonModel resjson = new JsonModel(true, "添加成功","200",patentTermWord);
            return JSON.toJSONString(resjson);
        } catch (Exception e) {
            logger.error("add core word method:"+e);
        }
        JsonModel resjson = new JsonModel(false, "添加词核心词失败","400",null);
        return JSON.toJSONString(resjson);
    }


    /**
     * 更新关键词接口
     * @param uid
     * @param featureWordDto
     * @return
     */
    @Override
    public String updateFeatureword(String uid ,FeatureWordDto featureWordDto) {


//        try {
//            String fid = featureWordDto.getFid();
//            List<String> corewords = featureWordDto.getCorewords();
//            List<String> featurewords = featureWordDto.getFeaturewords();
//
//            PatentFeatureWord patentFeatureWord = patentFeatureWordMapper.selectByPrimaryKey(fid);
//            if (patentFeatureWord ==null){
//                JsonModel json = new JsonModel(false, "此特征词记录不存在","400",null);
//                return JSON.toJSONString(json);
//            }
//            if (null != corewords && corewords.size()>0){
//                String cstring = String.join(",", corewords);
//                patentFeatureWord.setCorewords(cstring);
//            }
//            if (null != featurewords && featurewords.size()>0){
//                String fs = String.join(",", featurewords);
//                patentFeatureWord.setFeaturewords(fs);
//            }
//            int m = patentFeatureWordMapper.updateByPrimaryKeySelective(patentFeatureWord);
//            JsonModel resjson = new JsonModel(true, "更新特征词数据成功","200",null);
//            return JSON.toJSONString(resjson);
//        } catch (Exception e) {
//            logger.error("update FeatureWord method error :"+e);
//        }
        JsonModel resjson = new JsonModel(false, "更新特征词数据失败","400",null);
        return JSON.toJSONString(resjson);
    }
}
