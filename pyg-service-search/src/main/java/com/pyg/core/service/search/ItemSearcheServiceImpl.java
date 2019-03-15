package com.pyg.core.service.search;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.pyg.core.dao.item.ItemDao;
import com.pyg.core.pojo.item.Item;
import com.pyg.core.pojo.item.ItemQuery;
import com.pyg.core.service.search.ItemSearchService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ItemSearcheServiceImpl implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    @Resource
    private ItemDao itemDao;

    /**
     * 前台检索系统
     * @param searchMap
     * @return
     */
    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        // 封装所有结果集
        Map<String,Object> resultMap = new HashMap<>();
        String keywords = searchMap.get("keywords");
        if(keywords != null && !"".equals(keywords)){
            keywords = keywords.replace(" ", "");
            searchMap.put("keywords",keywords);
        }
        // 1,根据关键字检索,并结果分页
        //Map<String,Object> map = searchForPage(searchMap);
        Map<String,Object> map = searchForHighlightPage(searchMap);
        resultMap.putAll(map);
        // 2,加载商品分类
        List<String> categroyList = searchGroupPage(searchMap);
        resultMap.put("categoryList",categroyList);
        // 3,默认加载第一个分类下的商品及规格
        if(categroyList != null && categroyList.size()>0){
            Map<String,Object> brandAndSpecMap = defaultSelectBrandAndSpecByCategoryByName(categroyList.get(0));
            resultMap.putAll(brandAndSpecMap);
        }
        return resultMap;
    }

    /**
     * 商品上架
     * @param id
     */
    @Override
    public void isShow(Long id) {
        // 将该商品对应的库存中价格最低的sku保存到索引库中
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1")
                .andIsDefaultEqualTo("1").andNumGreaterThan(0);
        List<Item> items = itemDao.selectByExample(itemQuery);
        if(items != null && items.size()>0){ //判断items不为空
            for (Item item : items) {
                // 处理动态字段
                String spec = item.getSpec();
                Map<String,String> specMap = JSON.parseObject(spec, Map.class);
                item.setSpecMap(specMap);
            }
            // 将数据保存至索引库
            solrTemplate.saveBeans(items);
            // 提交操作
            solrTemplate.commit();

        }
    }

    @Override
    public void deleteItemForSolr(Long id) {
        // 删除索引库中的商品（下架）
        SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
        solrTemplate.delete(query);
        solrTemplate.commit();
    }

    /**
     * 默认加载第一个分类下的商品及规格
     * @param categoryName
     * @return
     */
    private Map<String,Object> defaultSelectBrandAndSpecByCategoryByName(String categoryName) {
        // 1,通过商品分类获取模板id
        Object typeId = redisTemplate.boundHashOps("itemCat").get(categoryName);
        // 2.通过模板id获取品牌结果集
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        // 3,通过模板id获取规格结果集
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        // 4,封装数据
        Map<String,Object> map = new HashMap<>();
        map.put("brandList",brandList);
        map.put("specList",specList);
        return map;
    }

    /**
     * 加载商品分类
     * @param searchMap
     * @return
     */
    private List<String> searchGroupPage(Map<String,String> searchMap) {
        // 1,封装检索条件
        Criteria criteria = new Criteria("item_keywords");// 指定检索字段
        String keywords = searchMap.get("keywords");
        if(keywords != null && !"".equals(keywords)){
            criteria.is(keywords);// 会且此,根据词条搜索
        }

        SimpleQuery simpleQuery = new SimpleQuery(criteria);
        // 2,设置分组条件
        GroupOptions groupOptions = new GroupOptions();
        groupOptions.addGroupByField("item_category"); // 根据该字段分组
        simpleQuery.setGroupOptions(groupOptions);

        List<String> list = new ArrayList<>();
        // 3,根据条件查询
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(simpleQuery, Item.class);
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String groupValue = groupEntry.getGroupValue(); // 分组结果
            list.add(groupValue);
        }
        // 4,封装结果集
        return list;
    }

    /**
     * 根据关键字搜索并且结果集分页:关键字高亮
     * @param searchMap
     * @return
     */
    private Map<String,Object> searchForHighlightPage(Map<String,String> searchMap) {
        // 1,封装检索条件
        Criteria criteria = new Criteria("item_keywords");// 指定检索字段
        String keywords = searchMap.get("keywords");
        if(keywords != null && !"".equals(keywords)){
            criteria.is(keywords);// 根据词条搜索(会切词)
        }
        SimpleHighlightQuery simpleHighlightQuery = new SimpleHighlightQuery(criteria);
        // 2,封装分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer startRow = (pageNo - 1) * pageSize;
        simpleHighlightQuery.setOffset(startRow); // 封装起始行
        simpleHighlightQuery.setRows(pageSize); // 封装每页条数
        // 3,封装高亮条件
        HighlightOptions highlightOptions = new HighlightOptions();
        highlightOptions.addField("item_title"); // 如果标题中有关键字,高亮显示
        highlightOptions.setSimplePrefix("<font color='red'>"); //关键字前缀
        highlightOptions.setSimplePostfix("</font>"); // 关键字后缀
        simpleHighlightQuery.setHighlightOptions(highlightOptions);
        // 封装筛选条件: 分类,品牌,规格,价格
        // 封装分类:category
        String category = searchMap.get("category");
        if(category != null && !"".equals(category)){
            Criteria cri = new Criteria("item_category");
            cri.is(category);
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(cri);
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
        }
        // 封装品牌:brand
        String brand = searchMap.get("brand");
        if(brand != null && !"".equals(brand)){
            Criteria cri = new Criteria("item_brand");
            cri.is(brand);
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(cri);
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
        }
        // 封装规格:spec
        String spec = searchMap.get("spec");
        if(spec != null && !"".equals(spec)){
            // 索引库规格选项字段为动态字段: item_spec_*
            Map<String,String> map = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entries = map.entrySet();
            for (Map.Entry<String, String> entry : entries) {
                Criteria cri = new Criteria("item_spec_" + entry.getKey());
                cri.is(entry.getValue());
                SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(cri);
                simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
            }
        }
        // 封装价格: price
        String price = searchMap.get("price");
        if (price != null && !"".equals(price)){
            String[] prices = price.split("-");
            Criteria cri = new Criteria("item_price");
            if(price.contains("*")){ // *** 以上
                cri.greaterThanEqual(prices[0]);
            }else{ // min-max
                cri.between(prices[0],prices[1],true,true);
            }
            SimpleFilterQuery simpleFilterQuery = new SimpleFilterQuery(cri);
            simpleHighlightQuery.addFilterQuery(simpleFilterQuery);
        }
        // 根据新品,价格排序
        // sortFiled: 排序的字段 sort: 排序的规格(DESC/ASC)
        String sort = searchMap.get("sort");
        if(sort != null && !"".equals(sort)){
            if("ASC".equals(sort)){
                Sort s = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortFiled"));
                simpleHighlightQuery.addSort(s);
            }else{
                Sort s = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortFiled"));
                simpleHighlightQuery.addSort(s);
            }
        }
        // 4,根据条件查询
        HighlightPage<Item> items = solrTemplate.queryForHighlightPage(simpleHighlightQuery, Item.class);
        // 处理高亮结果
        List<HighlightEntry<Item>> highlighted = items.getHighlighted();
        if(highlighted != null && highlighted.size()>0){
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                Item item = itemHighlightEntry.getEntity(); // 普通标题
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights(); // 高亮的标题
                if(highlights != null && highlights.size()>0){
                    String title = highlights.get(0).getSnipplets().get(0); //取出高亮标题
                    item.setTitle(title); // 替换没有高亮的标题
                }
            }
        }
        // 5,封装结果集
        Map<String,Object> map = new HashMap<>();
        map.put("totalPages",items.getTotalPages()); // 总页数
        map.put("total",items.getTotalElements()); // 总条数
        map.put("rows",items.getContent()); // 商品列表结果集
        return map;
    }

    /**
     * 根据关键字搜索并且结果集分页
     * @param searchMap
     * @return
     */
    private Map<String,Object> searchForPage(Map<String,String> searchMap) {
        // 1,封装检索条件
        Criteria criteria = new Criteria("item_keywords"); // 指定检索字段
        String keywords = searchMap.get("keywords");
        if(keywords != null && !"".equals(keywords)){
            criteria.is(keywords); // 会切词,根据词条检索
        }
        SimpleQuery simpleQuery = new SimpleQuery(criteria);
        // 2,封装分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        Integer startRow = (pageNo - 1) * pageSize;
        simpleQuery.setOffset(startRow); // 封装起始行
        simpleQuery.setRows(pageSize); // 封装每页条数
        // 3,根据条件查询
        ScoredPage<Item> scoredPage = solrTemplate.queryForPage(simpleQuery, Item.class);
        // 4,封装结果集
        Map<String,Object> map = new HashMap<>();
        map.put("totalPages",scoredPage.getTotalPages()); // 总页数
        map.put("total",scoredPage.getTotalElements()); // 总条数
        map.put("rows",scoredPage.getContent()); // 商品列表结果集
        return map;
    }



}
