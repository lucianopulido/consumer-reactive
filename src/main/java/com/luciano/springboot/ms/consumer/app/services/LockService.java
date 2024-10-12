package com.luciano.springboot.ms.consumer.app.services;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockService {

    private final JedisPool jedisPool;

    @Autowired
    public LockService(JedisPool jedisPool) {
        this.jedisPool = jedisPool;
    }

    public boolean acquireLock(String clientId) {
        try (Jedis redisClient = jedisPool.getResource()) {
            String lockKey = "lock:" + clientId;
            long expirationTime = System.currentTimeMillis() + 5000;
            if (redisClient.setnx(lockKey, String.valueOf(expirationTime)) == 1) {
                redisClient.expire(lockKey, 5);
                return true;
            }

            String currentLockValue = redisClient.get(lockKey);
            if (currentLockValue != null && Long.parseLong(currentLockValue) < System.currentTimeMillis()) {
                redisClient.set(lockKey, String.valueOf(expirationTime));
                redisClient.expire(lockKey, 5);
                return true;
            }
            return false;
        }
    }

    public void releaseLock(String clientId) {
        try (Jedis redisClient = jedisPool.getResource()) {
            String lockKey = "lock:" + clientId;
            redisClient.del(lockKey);
        }
    }
}
