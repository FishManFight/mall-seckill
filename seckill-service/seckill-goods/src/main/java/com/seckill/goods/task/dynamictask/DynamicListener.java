package com.seckill.goods.task.dynamictask;

import com.dangdang.ddframe.job.executor.ShardingContexts;
import com.dangdang.ddframe.job.lite.api.listener.AbstractDistributeOnceElasticJobListener;
import com.seckill.common.util.TimeUtil;

import java.util.Date;

/**
 * 监听器采用AOP模式，类似前置通知和后置通知，
 * doBeforeJobExecutedAtLastStarted和doAfterJobExecutedAtLastCompleted
 * 分别会在任务执行前和执行后调用，我们创建一个监听器实现任务调度前后拦截
 */
public class DynamicListener extends AbstractDistributeOnceElasticJobListener {

    /***
     * 构造函数
     * @param startedTimeoutMilliseconds
     * @param completedTimeoutMilliseconds
     */
    public DynamicListener(long startedTimeoutMilliseconds, long completedTimeoutMilliseconds) {
        super(startedTimeoutMilliseconds, completedTimeoutMilliseconds);
    }

    /***
     * 执行前通知
     * @param shardingContexts
     */
    @Override
    public void doBeforeJobExecutedAtLastStarted(ShardingContexts shardingContexts) {
        System.out.println("doBeforeJobExecutedAtLastStarted" + TimeUtil.date2FormatHHmmss(new Date()));
    }

    /***
     * 执行后通知
     * @param shardingContexts
     */
    @Override
    public void doAfterJobExecutedAtLastCompleted(ShardingContexts shardingContexts) {
        System.out.println("doAfterJobExecutedAtLastCompleted" + TimeUtil.date2FormatHHmmss(new Date()));
    }
}
