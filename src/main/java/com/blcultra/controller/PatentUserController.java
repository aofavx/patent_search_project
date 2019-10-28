package com.blcultra.controller;

import com.blcultra.service.PatentUserService;
import com.blcultra.support.Anoymous;
import com.blcultra.support.RequestData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用户服务
 * Created by sgy05 on 2019/8/5.
 */
@RestController
@CrossOrigin
@RequestMapping(value = "/patent/api/")
public class PatentUserController extends BaseController {


    @Autowired
    private PatentUserService patentUserService;

    /**
     * 用户登录
     * @param userName
     * @param userPwd
     * @return
     */
    @Anoymous
    @PostMapping(value = "login",produces = "application/json;charset=UTF-8")
    @ResponseBody
    public String login(@RequestParam(value="userName",required = true) String userName,
                        @RequestParam(value="userPwd" ,required = true) String userPwd) {
        log.info("UserController  login接口：用户名：{}, 密码：{} ",userName,userPwd);
        String res = patentUserService.login(userName.trim(),userPwd.trim());
        return res;
    }

}
