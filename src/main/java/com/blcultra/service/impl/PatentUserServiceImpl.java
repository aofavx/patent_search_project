package com.blcultra.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.blcultra.dao.PatentUserMapper;
import com.blcultra.model.PatentUser;
import com.blcultra.service.PatentUserService;
import com.blcultra.support.JsonModel;
import com.blcultra.util.JWTUtils;
import com.blcultra.util.MD5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户服务接口
 * Created by sgy05 on 2019/8/5.
 */
@Service(value = "patentUserService")
public class PatentUserServiceImpl implements PatentUserService {

    @Autowired
    private PatentUserMapper patentUserMapper;

    /**
     * 用户登录逻辑
     * @param loginname
     * @param loginpass
     * @return
     */
    @Override
    public String login(String loginname, String loginpass) {

        try {
            boolean b = beforeLoginValidate(loginname, loginpass);
            if (!b){
                JsonModel json = new JsonModel(false, "参数错误", "400",null);
                return JSON.toJSONString(json);
            }
            PatentUser patentUser = patentUserMapper.selectUserByUserName(loginname.trim());
            if (null == patentUser){
                JsonModel json = new JsonModel(false, "该用户不存在", "400",null);
                return JSON.toJSONString(json);
            }
            if(patentUser.getUstate().equals("0")){
                JsonModel json = new JsonModel(false, "该用户已被禁止登录","400",null);
                return JSON.toJSONString(json);
            }
            if (!MD5Util.verify(loginpass,loginname,patentUser.getPass())) {
                JsonModel json = new JsonModel(false, "用户名或密码错误","400",null);
                return JSON.toJSONString(json);
            }

            Map<String,Object> resultmap = new HashMap<>();
            resultmap.put("userid",patentUser.getUserid());
            resultmap.put("username",patentUser.getUsername());
            resultmap.put("udesc",patentUser.getUdesc());
            resultmap.put("telephone",patentUser.getTelephone());

            Map<String ,Object> map = JWTUtils.getInstance().getToken(patentUser.getUserid());
            resultmap.put("token",map.get("token"));
            resultmap.put("expires_in",map.get("expires_in"));

            JsonModel json = new JsonModel(true, "登录成功","200",resultmap);
            return JSON.toJSONString(json);
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonModel json = new JsonModel(false, "登录失败","400",null);
        return JSON.toJSONString(json);
    }

    private boolean beforeLoginValidate(String loginname,String loginpass){
        //验证参数
        if(StringUtils.isEmpty(loginname) || StringUtils.isEmpty(loginpass)){
            JSONObject error = new JSONObject();
            if(StringUtils.isEmpty(loginname)){
                error.put("loginName", "登录账号未填写");
            }
            if(StringUtils.isEmpty(loginpass)){
                error.put("loginPass", "登录密码未填写");
            }
            return false;
        }
        return true;
    }
}
