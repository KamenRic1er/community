package com.nowcoder.community.aspect;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.HostHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author: 少不入川
 * @Date: 2023/7/22 15:35
 */

@Component
@Aspect
public class DataAspect {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;

    @Pointcut("execution(* com.nowcoder.community.controller.*.*(..))")
    public void dataPointcut() {}


    // 所有Controller方法执行前记录UV、DAU
    @Before("dataPointcut()")
    public void preHandle(JoinPoint joinPoint){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        // 统计UV（独立访问）
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        // 统计DAU（日活跃用户）
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }
    }

}
