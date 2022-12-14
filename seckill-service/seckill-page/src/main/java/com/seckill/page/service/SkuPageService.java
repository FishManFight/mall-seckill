package com.seckill.page.service;

import java.util.Map;

public interface SkuPageService {

    /***
     * 删除商品静态页
     * @param name 静态页名称
     * @param htmlPath 静态页存储路径
     */
    void delHtml(String name, String htmlPath);

    /***
     * 删除所有商品静态页
     */
    void delAllHtml(String htmlPath);

    /****
     * 生成静态页
     * @param dataMap
     */
    void itemPage(Map<String, Object> dataMap) throws Exception;
}
