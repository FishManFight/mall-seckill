package com.seckill.monitor;

import com.dangdang.ddframe.job.api.ShardingContext;
import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.elasticjob.lite.annotation.ElasticSimpleJob;
import com.seckill.goods.feign.SkuFeign;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Component
@ElasticSimpleJob(
        cron = "0/5 * * * * ?",
        jobName = "monitortask",
        shardingTotalCount = 1
)
public class MonitorTask implements SimpleJob {

    @Autowired
    private MonitorItemsAccess monitorItemsAccess;

    @Autowired
    private SkuFeign skuFeign;

    /***
     * 执行业务逻辑
     * @param shardingContext
     */
    @SneakyThrows
    @Override
    public void execute(ShardingContext shardingContext) {
        List<String> list = monitorItemsAccess.currentTime();
        for (String time : list) {
            System.out.println(time);
        }
        List<String> ids = monitorItemsAccess.loadData();
        if (!CollectionUtils.isEmpty(ids)) {
            for (String id : ids) {
                System.out.println("热点商品ID：" + id);
            }
            // 热点数据隔离
            skuFeign.hotIsolation(ids);
        }
    }
}
