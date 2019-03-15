package com.pyg.core.controller.content;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.pojo.ad.Content;
import com.pyg.core.service.content.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/content")
public class ContentController {

    @Reference
    private ContentService contentService;

    /**
     * 首页焦点图
     * @param categoryId
     * @return
     */
    @RequestMapping("/findByCategoryId.do")
    public List<Content> findByCategoryId(Long categoryId){
        return contentService.findByCategoryId(categoryId);
    }
}
