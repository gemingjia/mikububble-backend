package com.miku.bubble.common;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miku.bubble.model.entity.User;
import com.miku.bubble.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author Ge Mingjia
 */
@Component
@Slf4j
public class PreCacheJob {

    @Resource
    private UserService userService;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private RedissonClient redissonClient;

    private List<Long> mainUserList = Arrays.asList(1L);

    /**
     * 每天执行 缓存预热推荐用户
     */
    @Scheduled(cron = "0 30 20 * * *")
    public void doCacheRecommendUser(){
        // 使用分布式锁解决多个服务器同时执行预热的问题
        RLock lock = redissonClient.getLock("mikububble_db:precachejob:docache:lock");
        try{
            if(lock.tryLock(0,-1,TimeUnit.MILLISECONDS)){
                for (Long userId : mainUserList) {
                    QueryWrapper<User> queryWrapper = new QueryWrapper<>();
                    Page<User> userPage = userService.page(new Page<>(1, 20), queryWrapper);
                    String redisKey = String.format("mikububble_db:user:recommend:%s", userId);
                    ValueOperations<String, Object> valueOperations = redisTemplate.opsForValue();
                    try {
                        valueOperations.set(redisKey,userPage,300000, TimeUnit.MILLISECONDS);
                    } catch (Exception e) {
                        log.error("Redis error");
                    }
                }
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        } finally {
            // redis集群数据同步问题：红锁
            lock.unlock();
        }

    }
}
