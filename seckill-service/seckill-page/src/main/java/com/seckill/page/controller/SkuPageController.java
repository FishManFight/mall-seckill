package com.seckill.page.controller;

import com.alibaba.fastjson.JSON;
import com.seckill.common.util.StatusCode;
import com.seckill.goods.feign.SkuFeign;
import com.seckill.goods.pojo.Sku;
import com.seckill.page.service.SkuPageService;
import com.seckill.common.util.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/page")
public class SkuPageController {

    @Autowired
    private SkuPageService skuPageService;

    @Value("${htmlPath}")
    private String htmlPath;

    @Autowired
    private SkuFeign skuFeign;

    /****
     * 删除商品详情页
     */
    @DeleteMapping(value = "/html/{id}")
    public Result delHtml(@PathVariable(value = "id") String id) {
        String fileName = id + ".html";
        skuPageService.delHtml(fileName, htmlPath);
        return new Result(true, StatusCode.OK, "删除静态页成功！");
    }


    /****
     * 静态页生成测试
     */
    @PostMapping(value = "/html")
    public Result html(@RequestBody Sku sku) throws Exception {
        // 数据模型
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("path", htmlPath);
        dataMap.put("templateName", "items.ftl");
        dataMap.put("name", sku.getId() + ".html");// id.html
        dataMap.put("address", "北京");
        dataMap.put("sku", sku);

        // 生成静态页
        skuPageService.itemPage(dataMap);
        return new Result(true, StatusCode.OK, "生成静态页成功！");
    }


    /****
     * 静态页生成
     */
    @GetMapping(value = "/html/{id}")
    public Result html(@PathVariable(value = "id") String id) throws Exception {
        Result<Sku> skuResult = skuFeign.findById(id);
        Sku sku = skuResult.getData();
        // 数据模型
        Map<String, Object> dataMap = new HashMap<String, Object>();
        dataMap.put("path", htmlPath);
        dataMap.put("templateName", "items.ftl");
        dataMap.put("name", id + ".html");// id.html
        dataMap.put("address", "北京");
        dataMap.put("sku", sku);
        dataMap.put("images", sku.getImages().split(","));
        // 获取spec
        String spec = sku.getSpec();
        Map<String, String> specMap = JSON.parseObject(spec, Map.class);
        String speclist = "";
        for (Map.Entry<String, String> entry : specMap.entrySet()) {
            speclist += "/" + entry.getValue();
        }
        dataMap.put("spec", speclist);

        // 生成静态页
        skuPageService.itemPage(dataMap);
        return new Result(true, StatusCode.OK, "生成静态页成功！");
    }


    /***
     * 生成所有商品静态页
     */
    @GetMapping(value = "/all/html")
    public Result allhtml() throws Exception {
        // 分页数据
        int page = 1, size = 500;

        // 1.查询总记录数
        Integer total = skuFeign.count();

        // 2.根据总记录数计算总页数
        int totalpages = total % size == 0 ? total / size : (total / size) + 1;

        // 3.循环总页数，查询每页的数据
        for (int i = 0; i < totalpages; i++) {
            // 测试：只生成500个静态页
            List<Sku> skus = skuFeign.list(page, size);
            // 循环生成静态页
            for (Sku sku : skus) {
                // 数据模型
                try {
                    Map<String, Object> dataMap = new HashMap<String, Object>();
                    dataMap.put("path", htmlPath);
                    dataMap.put("templateName", "items.ftl");
                    dataMap.put("name", sku.getId() + ".html");// id.html
                    dataMap.put("address", "北京");
                    dataMap.put("sku", sku);
                    dataMap.put("images", sku.getImages().split(","));
                    // 获取spec
                    String spec = sku.getSpec();
                    Map<String, String> specMap = JSON.parseObject(spec, Map.class);
                    String speclist = "";
                    for (Map.Entry<String, String> entry : specMap.entrySet()) {
                        speclist += "/" + entry.getValue();
                    }
                    dataMap.put("spec", speclist);

                    // 生成静态页
                    skuPageService.itemPage(dataMap);
                } catch (Exception e) {
                }
            }
            page++;
        }
        return new Result(true, StatusCode.OK, "生成静态页成功！");
    }

    /****
     * 删除所有详情页
     */
    @DeleteMapping(value = "/all")
    public Result delAllHtml() {
        skuPageService.delAllHtml(htmlPath);
        return new Result(true, StatusCode.OK, "删除所有静态页成功！");
    }
}
