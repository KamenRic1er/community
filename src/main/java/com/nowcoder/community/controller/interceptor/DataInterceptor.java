package com.nowcoder.community.controller.interceptor;

import com.nowcoder.community.entity.User;
import com.nowcoder.community.service.DataService;
import com.nowcoder.community.util.HostHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Deprecated
//@Component
public class DataInterceptor implements HandlerInterceptor {

    @Autowired
    private DataService dataService;

    @Autowired
    private HostHolder hostHolder;


    // 所有Controller方法执行前记录UV、DAU
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 统计UV（独立访问）
        String ip = request.getRemoteHost();
        dataService.recordUV(ip);

        // 统计DAU（日活跃用户）
        User user = hostHolder.getUser();
        if (user != null) {
            dataService.recordDAU(user.getId());
        }

        return true;
    }
}
