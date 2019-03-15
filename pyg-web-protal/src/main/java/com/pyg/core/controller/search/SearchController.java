package com.pyg.core.controller.search;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.service.search.ItemSearchService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/itemsearch")
public class SearchController {

    @Reference
    private ItemSearchService itemSearchService;

    /**
     * 前台检索
     * @param searchMap
     * @return
     */
    @RequestMapping("/search.do")
    public Map<String,Object> search(@RequestBody Map<String,String> searchMap){
        return itemSearchService.search(searchMap);
    }
}
