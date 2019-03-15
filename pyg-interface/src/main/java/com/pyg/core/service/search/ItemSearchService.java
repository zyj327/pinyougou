package com.pyg.core.service.search;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 前台系统检索
     * @param searchMap
     * @return
     */
    public Map<String, Object> search(Map<String,String> searchMap);

    /**
     * 商品上架:索引库添加数据
     * @param id
     */
    public void isShow(Long id);

    /**
     * 商品下架:删除索引库数据
     */
    public void deleteItemForSolr(Long id);

}
