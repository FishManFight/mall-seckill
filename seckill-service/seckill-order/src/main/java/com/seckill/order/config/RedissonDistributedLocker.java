package com.seckill.order.config;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedissonDistributedLocker implements DistributedLocker {

    @Autowired
    private RedissonClient redissonClient;

    /***
     * 加锁,会一直循环加锁，直到拿到锁
     * @param lockKey
     * @return
     */
    @Override
    public RLock lock(String lockKey) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock();
        return lock;
    }

    /***
     * 加锁,在指定时间内拿不到锁就会放弃,单位为秒
     * @param lockKey
     * @return
     */
    @Override
    public RLock lock(String lockKey, long timeout) {
        RLock lock = redissonClient.getLock(lockKey);
        lock.lock(timeout, TimeUnit.SECONDS);
        return lock;
    }

    /***
     * 加锁,在指定时间内拿不到锁就会放弃
     * timeout为加锁时间，时间单位由unit确定
     * @param lockKey
     * @return
     */
    @Override
    public RLock lock(String lockKey, long timeout, TimeUnit unit) {
        return null;
    }

    /***
     * tryLock()，马上返回，拿到lock就返回true，不然返回false。
     * 带时间限制的tryLock()，拿不到lock，就等一段时间，超时返回false.
     * @param lockKey
     * @return
     */
    @Override
    public boolean tryLock(String lockKey, long waitTime, long leaseTime, TimeUnit unit) {
        RLock lock = redissonClient.getLock(lockKey);
        try {
            return lock.tryLock(waitTime, leaseTime, unit);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /****
     * 解锁
     * @param lockkey
     */
    @Override
    public void unLock(String lockkey) {
        RLock lock = redissonClient.getLock(lockkey);
        lock.unlock();
    }

    /***
     * 解锁
     * @param lock
     */
    @Override
    public void unLocke(RLock lock) {
        lock.unlock();
    }
}
