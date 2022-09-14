package com.seckill.goods.task.dynamictask;

import com.dangdang.ddframe.job.api.simple.SimpleJob;
import com.dangdang.ddframe.job.config.JobCoreConfiguration;
import com.dangdang.ddframe.job.config.simple.SimpleJobConfiguration;
import com.dangdang.ddframe.job.lite.config.LiteJobConfiguration;
import com.dangdang.ddframe.job.lite.spring.api.SpringJobScheduler;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperConfiguration;
import com.dangdang.ddframe.job.reg.zookeeper.ZookeeperRegistryCenter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.text.SimpleDateFormat;
import java.util.Date;

@Configuration
public class ElasticjobDynamicConfig {

    // 配置文件中的zookeeper的ip和端口
    @Value("${zkserver}")
    private String zkserver;
    // 指定一个命名空间
    @Value("${zknamespace}")
    private String zknamespace;

    /****
     * 1.配置初始化数据
     * 配置Zookeeper和namespace
     */
    @Bean
    public ZookeeperConfiguration zkConfig() {
        // 1.Zookeeper地址
        // 2.定时任务命名空间
        return new ZookeeperConfiguration(zkserver, zknamespace);
    }

    /****
     * 2.注册初始化数据
     * 向zookeeper注册初始化信息
     */
    @Bean(initMethod = "init")
    public ZookeeperRegistryCenter registryCenter(ZookeeperConfiguration zkConfig) {
        return new ZookeeperRegistryCenter(zkConfig);
    }

    /****
     * 监听器
     * 创建ElasticJob的监听器实例
     */
    @Bean
    public DynamicListener dynamicListener() {
        // 初始化要给定超时多少秒重连
        return new DynamicListener(10000L, 100000L);
    }

    @Autowired
    private DynamicListener dynamicListener;

    @Autowired
    private ZookeeperRegistryCenter zookeeperRegistryCenter;

    /**
     * 3.动态添加定时任务案例
     *
     * @param jobName:任务的命名空间
     * @param cron：定时周期表达式
     * @param shardingTotalCount：分片个数
     * @param instance:执行的定时任务对象
     * @param id：自定义参数
     * @return
     */
    public void addDynamicTask(String jobName, String cron, int shardingTotalCount, String id, SimpleJob instance) {
        // 1.添加Elastjob-lite的任务作业器
        // 创建任务构建对象
        LiteJobConfiguration liteJobConfiguration = LiteJobConfiguration.newBuilder(
                        new SimpleJobConfiguration(
                                JobCoreConfiguration
                                        // 任务命名空间名字、任务执行周期表达式、分片个数
                                        .newBuilder(jobName, cron, shardingTotalCount)
                                        // 额外的参数
                                        .jobParameter(id)
                                        .build(),
                                instance.getClass().getName())
                ).overwrite(true)// 本地配置是否可覆盖注册中心配置
                .build();

        // 2.将Lite的任务作业器添加到Spring的任务启动器中，并初始化
        new SpringJobScheduler(instance, zookeeperRegistryCenter, liteJobConfiguration, dynamicListener).init();
    }

    // cron表达式格式
    private static String cron = "ss mm HH dd MM ? yyyy";

    /****
     * 时间转换成Cron表达式
     * "1/5 * * * * ?";
     */
    public static String date2cron(Date date) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(cron);
        return simpleDateFormat.format(date);
    }
}
