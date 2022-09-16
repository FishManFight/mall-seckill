package com.seckill.search.dao;

import com.seckill.search.pojo.SkuInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface SkuInfoMapper extends ElasticsearchRepository<SkuInfo, String> {
    /***
     * 根据bgtime实现分页查询
     * @param of
     * @param starttime
     * @return
     */
    Page<SkuInfo> findByBgtime(String starttime, PageRequest of);
    Page<SkuInfo> findByName(String name, PageRequest of);
}
