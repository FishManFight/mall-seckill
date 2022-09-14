package com.seckill.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.seckill.goods.feign.SkuFeign;
import com.seckill.goods.pojo.Sku;
import com.seckill.search.dao.SkuInfoMapper;
import com.seckill.search.pojo.SkuInfo;
import com.seckill.search.service.SkuInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class SkuInfoServiceImpl implements SkuInfoService {

    @Autowired
    private SkuInfoMapper skuInfoMapper;

    @Autowired
    private SkuFeign skuFeign;

    /***
     * 秒杀搜索列表
     * @param searchMap
     * @return
     */
    @Override
    public Page<SkuInfo> search(Map<String, String> searchMap) {
        // 时间  starttime
        String starttime = searchMap.get("starttime");
        if (!StringUtils.isEmpty(starttime)) {
            starttime = parseTime(starttime);
        }
        // 根据bgtime实现分页搜索
        return skuInfoMapper.findByBgtime(starttime, PageRequest.of(pageConveter(searchMap) - 1, 20));// 当前页对应的信息,bgtime
    }

    /***
     * 获取当前页->pageNum
     */
    public Integer pageConveter(Map<String, String> searchMap) {
        try {
            return Integer.parseInt(searchMap.get("pageNum"));
        } catch (NumberFormatException e) {
            return 1;
        }
    }

    /***
     * 单条数据增量操作  ->删除索引   type=3
     *           ->修改索引   type=2
     *           ->添加索引   type=1
     */
    @Override
    public void modify(Integer type, SkuInfo skuInfo) {
        if (type == 1 || type == 2) {
            System.out.println("Search:" + skuInfo.getId() + "," + skuInfo.getBgtime());
            // 时间转换
            skuInfoConverter(skuInfo);
            // 增加到引索库
            skuInfoMapper.save(skuInfo);
        } else {
            skuInfoMapper.deleteById(skuInfo.getId());
        }
    }

    /***
     * 批量导入
     */
    @Override
    public void addAll() {
        // 分页数据,从第1页开始，每页处理500条
        int pageNum = 1;
        int pageSize = 500;

        // 1.查询总记录数
        Integer total = skuFeign.count();

        // 2.根据总记录数计算总页数
        int totalPages = total % pageSize == 0 ? total / pageSize : (total / pageSize) + 1;

        // 3.循环总页数，查询每页的数据
        for (int i = 0; i < totalPages; i++) {
            List<Sku> skus = skuFeign.list(pageNum, pageSize);
            // 4.将数据转换成SkuInfo
            List<SkuInfo> skuInfos = JSON.parseArray(JSON.toJSONString(skus), SkuInfo.class);
            // 将开始时间转换成字符串类型
            for (SkuInfo skuInfo : skuInfos) {
                skuInfoConverter(skuInfo);
            }
            // 批量保存
            skuInfoMapper.saveAll(skuInfos);
            // 页数递增
            pageNum++;
        }
    }

    public static void skuInfoConverter(SkuInfo skuInfo) {
        // 获取秒杀时间
        Date seckillBegin = skuInfo.getSeckillBegin();
        if (seckillBegin != null) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String bgtime = simpleDateFormat.format(skuInfo.getSeckillBegin());
            skuInfo.setBgtime(bgtime);
        }
    }

    @Override
    public void deleteAll() {
        skuInfoMapper.deleteAll();
    }

    // 时间转换
    public String parseTime(String time) {
        try {
            // 202207301600
            if (time.length() > 12) {
                time = time.substring(0, 12);
            }
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmm");
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyyMMddHH");
            Date date = simpleDateFormat.parse(time);
            return simpleDateFormat1.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void main(String[] args) throws ParseException {
        String str = "202207301600";
        str = str.substring(0, 12);
        System.out.println(str);
    }
}
