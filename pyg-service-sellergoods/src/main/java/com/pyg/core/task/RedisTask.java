package com.pyg.core.task;

import com.alibaba.fastjson.JSON;
import com.pyg.core.dao.item.ItemCatDao;
import com.pyg.core.dao.specification.SpecificationOptionDao;
import com.pyg.core.dao.template.TypeTemplateDao;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.pojo.specification.SpecificationOption;
import com.pyg.core.pojo.specification.SpecificationOptionQuery;
import com.pyg.core.pojo.template.TypeTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import sun.dc.pr.PRError;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Component
public class RedisTask {

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Scheduled(cron = "0 0 10,14,16 * * ?")
    public void autoItemCatToRedis(){
        // 列表查询的过程中将数据同步到redis中
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if(itemCats != null && itemCats.size()>0){
            for (ItemCat itemCat : itemCats) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
            System.out.println("定时器执行啦!");
        }
    }

    @Scheduled(cron = "0 0 10,14,16 * * ?")
    public void setBrandsAndSpecsToRedis(){
        // 将模板数据放入缓存
        List<TypeTemplate> list = typeTemplateDao.selectByExample(null);
        if(list != null && list.size()>0){
            for (TypeTemplate template : list) {
                // 缓存该模板下的品牌
                List<Map> brandList = JSON.parseArray(template.getBrandIds(),Map.class);
                redisTemplate.boundHashOps("brandList").put(template.getId(),brandList);
                // 缓存该模板下的规格
                List<Map> specList = findBySpecList(template.getId());
                redisTemplate.boundHashOps("specList").put(template.getId(),specList);
            }
        }
    }

    public List<Map> findBySpecList(Long id) {
        // 通过id获取到模板
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        // 通过模板获取规格
        // [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        String specIds = typeTemplate.getSpecIds();
        // 将字符串转成对象
        List<Map> specList = JSON.parseArray(specIds, Map.class);
        // 设置规格选项
        if(specList != null && specList.size() > 0){
            for (Map map : specList) {
                // 获取规格id
                long specId = Long.parseLong(map.get("id").toString());
                // 获取对应的规格选项
                SpecificationOptionQuery query = new SpecificationOptionQuery();
                query.createCriteria().andSpecIdEqualTo(specId);
                List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
                // 将规格选择设置到map中
                map.put("options", options);
            }
        }
        return specList;
    }

}
