package com.nowcoder.community.controller.advice;

import com.nowcoder.community.util.CommunityUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @Author: 少不入川
 * @Date: 2023/1/17 11:06
 */
// 使用annotation参数意味着只扫描使用@Controller注解的Controller
// 注解@ControllerAdvice表示这是一个控制器增强类，当控制器发生异常且符合类中定义的拦截异常类，将会被拦截
@ControllerAdvice(annotations = Controller.class)
public class ExceptionAdvice {

    public static final Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

//    注解ExceptionHandler定义拦截的异常类
    @ExceptionHandler({Exception.class})
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) throws IOException {
        logger.error("服务器发生异常：" + e.getMessage());
        for(StackTraceElement element : e.getStackTrace()){
            logger.error(element.toString());
        }

        // 获得请求头信息
        String xRequestedWith = request.getHeader("x-requested-with");
        // XMLHttpRequest是属于AJAX的内容，所以下面的代码意思就是
        // 如果是ajax请求，就往响应体中写内容（错误返回信息），不是ajax请求就直接重定向跳转错误页面
        if("XMLHttpRequest".equals(xRequestedWith)){
            response.setContentType("application/plain;charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.write(CommunityUtil.getJSONString(1, "服务器异常!"));
        }else{
            response.sendRedirect(request.getContextPath() + "/error");
        }

    }

}
