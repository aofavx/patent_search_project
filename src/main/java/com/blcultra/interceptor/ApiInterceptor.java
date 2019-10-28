package com.blcultra.interceptor;

import com.blcultra.support.Anoymous;
import com.blcultra.util.JWTUtils;
import com.blcultra.util.ResponseUtil;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.lang.reflect.Method;

/**
 *拦截器工具类
 * Created by sgy05 on 2019/6/18.
 */
public class ApiInterceptor implements HandlerInterceptor {

    private final String ACCESS_TOKEN="Authorization";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {
        if(!(handler instanceof HandlerMethod)){
            return true;
        }
        HandlerMethod handlerMethod=(HandlerMethod)handler;
        if(isAnoymous(handlerMethod)){
            return true;
        }

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/json;charset=utf-8");
        String authorization=request.getHeader(ACCESS_TOKEN);
        if(StringUtils.isEmpty(authorization)){
            PrintWriter writer = response.getWriter();
            writer.write(ResponseUtil.fail(400,"非法请求"));
            writer.flush();
            return false;
        }
        JWTUtils.JWTResult result = JWTUtils.getInstance().checkToken(authorization);

        if (!result.isStatus()) {
            PrintWriter writer = response.getWriter();
            writer.write(ResponseUtil.fail(Integer.parseInt(result.getCode()),result.getMsg()));
            writer.flush();
            return false;
        }
        return true;
    }

    private boolean isAnoymous(HandlerMethod handlerMethod){
        Object bean=handlerMethod.getBean();
        Class clazz=bean.getClass();
        if(clazz.getAnnotation(Anoymous.class)!=null){
            return true;
        }
        Method method=handlerMethod.getMethod();
        return method.getAnnotation(Anoymous.class)!=null;
    }
}
