package com.seckill.goods.task.statictask;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import org.springframework.stereotype.Component;

/**
 * 静态任务测试
 * cron:定时表达式
 * jobName：这里和bootstrap.yml中的namespace保持一致
 * shardingTotalCount：分片数量
 */
@ElasticSimpleJob(cron = "0/30 * * * * ?", jobName = "updateTask")
@Component
public class StaticJobTask implements SimpleJob {
    @Override
    public void execute(ShardingContext shardingContext) {
        // System.out.println(new Date() + "--------ElasticjobStaticTask执行！");
    }
}
