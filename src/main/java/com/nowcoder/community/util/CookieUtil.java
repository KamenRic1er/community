package com.nowcoder.community.util;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

/**
 * @Author: 少不入川
 * @Date: 2023/1/13 17:46
 */
public class CookieUtil {

    public static String getValue(HttpServletRequest request, String name){
        if(request == null || name == null)
            throw new IllegalArgumentException("参数为空!");

        Cookie[] cookies = request.getCookies();
        if(cookies != null){
            for(Cookie cookie : cookies){
                if(cookie.getName().equals(name)){
                    return cookie.getValue();
                }
            }
        }
        return null;
     }
}
