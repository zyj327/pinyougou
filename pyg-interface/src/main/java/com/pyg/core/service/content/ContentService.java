package com.pyg.core.service.content;

import java.util.List;


import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.ad.Content;


public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

	/**
	 * 首页焦点图展示
	 * @param categoryId
	 * @return
	 */
	public List<Content> findByCategoryId(Long categoryId);
}
