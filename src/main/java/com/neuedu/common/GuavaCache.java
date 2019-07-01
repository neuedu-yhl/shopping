package com.neuedu.common;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GuavaCache {

    //LRU算法  最多使用次数算法
    //链式写法
    public static LoadingCache<String,String> cacheBuilder = CacheBuilder.newBuilder().initialCapacity(1000).maximumSize(1000).expireAfterAccess(1,TimeUnit.DAYS).build(
            new CacheLoader<String, String>() {
                @Override
                public String load(String key) throws Exception {
                    //默认方法
                    return "null";
                }
            }
    );

    public static void putCache(String key,String value)
    {
        cacheBuilder.put(key,value);
    }

    public static String getCache(String key)
    {
        String s = null;
        try {
            s = cacheBuilder.get(key);
            if("null".equals(s))
            {
                return null;
            }
            return s;
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return s;
    }











}
