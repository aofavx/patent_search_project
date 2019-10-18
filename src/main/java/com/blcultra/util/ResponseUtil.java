package com.blcultra.util;

import com.alibaba.fastjson.JSON;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 返回值工具类
 * Created by sgy05 on 2019/6/18.
 */
public class ResponseUtil {

    public static String ok() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 200);
        obj.put("flag",true);
        obj.put("msg", "成功");

        return JSON.toJSONString(obj);
    }
    public static String ok(String msg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 200);
        obj.put("flag",true);
        obj.put("msg", msg);

        return JSON.toJSONString(obj);
    }

    public static String ok(Object data) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 200);
        obj.put("flag",true);
        obj.put("msg", "成功");
        obj.put("data", data);
        return JSON.toJSONString(obj);
    }

    public static String ok(Object data,String msg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 200);
        obj.put("flag",true);
        obj.put("msg", msg);
        obj.put("data", data);
        return JSON.toJSONString(obj);
    }


    public static String okList(List list) {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("list", list);
        return ok(data);
    }

    public static String fail() {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", 400);
        obj.put("msg", "错误");
        obj.put("flag",false);
        return JSON.toJSONString(obj);
    }

    public static String fail(int errno, String errmsg) {
        Map<String, Object> obj = new HashMap<String, Object>();
        obj.put("code", errno);
        obj.put("msg", errmsg);
        obj.put("flag",false);
        obj.put("data", null);
        return JSON.toJSONString(obj);
    }

    public static String badArgument() {
        return fail(401, "参数不对");
    }
    public static String badArgument(String msg) {
        return fail(401, msg);
    }

    public static String badArgumentValue() {
        return fail(402, "参数值不对");
    }

    public static String unlogin() {
        return fail(501, "请登录");
    }

    public static String serious() {
        return fail(502, "系统内部错误");
    }

    public static String unsupport() {
        return fail(503, "业务不支持");
    }

    public static String updatedDateExpired() {
        return fail(504, "更新数据已经失效");
    }

    public static String updatedDataFailed() {
        return fail(505, "更新数据失败");
    }

    public static String unauthz() {
        return fail(506, "无操作权限");
    }


}
