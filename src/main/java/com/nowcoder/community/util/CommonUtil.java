package com.nowcoder.community.util;

import java.util.Random;

/**
 * @Author: 少不入川
 * @Date: 2023/7/22 21:31
 */
public class CommonUtil {

    private static final Random random = new Random();

    public static long getRandomExpireTime(long expireTime){
        return expireTime + (long) random.nextInt((int)expireTime / 10);
    }

}
