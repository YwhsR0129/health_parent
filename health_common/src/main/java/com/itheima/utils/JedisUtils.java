package com.itheima.utils;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import java.util.List;

public class JedisUtils {
    public static void updateRedis(JedisPool jedisPool, String key, List<String> items) {
        Jedis resource = jedisPool.getResource();
        for (String item : items) {
            if (item != null) {
                resource.lpop(key);
                resource.lpush(key, item);
            }
        }
    }
}
