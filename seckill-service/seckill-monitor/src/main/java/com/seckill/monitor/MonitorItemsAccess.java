package com.seckill.monitor;

import com.alibaba.druid.pool.DruidDataSource;
import com.seckill.common.util.TimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Component
public class MonitorItemsAccess {

    @Value("${druidurl}")
    private String druidurl;

    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private DruidDataSource dataSource;

    /******
     * 定义热点数据标准：
     *      1.某一件商品访问量>N
     *      2.最近N小时  设置为的最近1小时
     */
    public List<String> loadData() throws Exception {
        // 获取连接对象
        // Connection connection = (AvaticaConnection) DriverManager.getConnection(druidurl);
        Connection connection = dataSource.getConnection();
        // Statement
        Statement statement = connection.createStatement();

        // 执行查询
        ResultSet resultSet = statement.executeQuery(druidSQL());

        // 解析结果集
        List<String> ids = new ArrayList<String>();
        while (resultSet.next()) {
            // 获取uri,格式：/web/items/123456789.html
            String uri = resultSet.getString("uri");
            // 处理掉/web/items/和.html
            if (uri.startsWith("/web/items/") && uri.endsWith(".html")) {
                uri = uri.replaceFirst("/web/items/", "");
                uri = uri.substring(0, uri.length() - 5);
                ids.add(uri);
            }
        }
        // 关闭资源
        resultSet.close();
        statement.close();
        connection.close();
        return ids;
    }


    /***
     * SQL组装
     * @return
     */
    public String druidSQL() {

        // 1小时前的时间
        String onehourago = TimeUtil.date2FormatYYYYMMDDHHmmss(TimeUtil.addDateHour(new Date(), -1));

        // SQL语句
        String sql = "SELECT uri,count(*) AS \"viewcount\" FROM(SELECT * FROM \"itemaccess\" WHERE __time>'" + onehourago + "'";

        // 排除掉已经存在的数据
        // 加载所有热点秒杀商品的ID
        Set<String> keys = redisTemplate.keys("SKU_*");
        if (keys != null && keys.size() > 0) {
            StringBuffer buffer = new StringBuffer();
            for (String key : keys) {
                buffer.append("'/web/items/" + key.substring(4) + ".html',");
            }
            String ids = buffer.toString().substring(0, buffer.toString().length() - 1);

            // 组装SQL
            sql += " AND uri NOT IN(" + ids + ")";
        }

        // 排序部分组装
        sql += " ORDER BY __time DESC) GROUP BY uri HAVING \"viewcount\">2";
        return sql;
    }


    /**
     * 获取apache druid的当前时间
     *
     * @return
     * @throws SQLException
     */
    public List<String> currentTime() throws SQLException {
        // 获取连接对象
        Connection connection = dataSource.getConnection();
        // Statement
        Statement statement = connection.createStatement();
        // 执行查询
        ResultSet resultSet = statement.executeQuery("SELECT  CURRENT_TIMESTAMP ");
        // 解析结果集
        List<String> list = new ArrayList<String>();
        while (resultSet.next()) {
            String time = resultSet.getString(1);
            list.add(time);
        }
        // 关闭资源
        resultSet.close();
        statement.close();
        connection.close();
        return list;
    }
}
