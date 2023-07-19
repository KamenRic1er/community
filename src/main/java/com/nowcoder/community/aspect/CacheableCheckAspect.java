package com.nowcoder.community.aspect;

/**
 * @Author: 少不入川
 * @Date: 2023/7/19 21:54
 */

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 不要设置order，否则该AOP将会在@Cacheable之前执行，就无法检测缓存是否起作用了
 * 仅支持对方法级的@Cacheable进行检测，默认是以方法的所有参数作为key
 * 不支持缓存缓存更新，重新缓存的情况（会判断为缓存失效，可以自己完善实现处理@CacheEvict）
 */
//@Aspect
//@Component
//@ConditionalOnProperty(name = "cacheable.check.switch",havingValue = "1")
public class CacheableCheckAspect {
    //针对每个缓存，都有自己的一个set
    private Map<String, Set<String>> cache = new ConcurrentHashMap<>();
    //存放失效缓存的集合
    private Set<String> unValidCache = new HashSet<>();

    @Around("@annotation(cacheable)")
    public Object before(ProceedingJoinPoint joinPoint, Cacheable cacheable) throws Throwable{
        Object[] args = joinPoint.getArgs();
        String value = String.join("_", cacheable.value());
        if(!unValidCache.contains(value)) {
            if (!cache.containsKey(value)) {
                synchronized (cache) {
                    if (!cache.containsKey(value)) {
                        cache.put(value, new HashSet<>());
                    }
                }
            }
            String key = Arrays.stream(args).map(String::valueOf).collect(Collectors.joining("_"));
            Set<String> set = cache.get(value); //同一个缓存同一个key，如果多次调用了，说明缓存失效
            if (set.contains(key)) {
                System.err.println(value + "缓存无效，key:" + key);
                unValidCache.add(value);
            } else {
                set.add(key);
            }
        }
        try {
            return joinPoint.proceed();
        }finally {
        }
    }
}