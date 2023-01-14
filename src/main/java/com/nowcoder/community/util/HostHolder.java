package com.nowcoder.community.util;

import com.nowcoder.community.entity.User;
import org.springframework.stereotype.Component;

/**
 * @Author: 少不入川
 * @Date: 2023/1/13 18:01
 * @Comment: 用于持有用户信息，替代Session对象
 */
@Component
public class HostHolder {

    /**
     * todo 这个地方我觉得可以当一个点深挖，引出自己对juc的了解
     * */

    private ThreadLocal<User> users = new ThreadLocal<>();

    public void setUser(User user){
        users.set(user);
    }

    public User getUser(){
        return users.get();
    }

    public void clear(){
        users.remove();
    }

}
