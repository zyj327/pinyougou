package com.pyg.core.service.content;

import java.util.List;

import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.ad.ContentQuery;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;

import com.pyg.core.dao.ad.ContentDao;
import com.pyg.core.pojo.ad.Content;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class ContentServiceImpl implements ContentService {

	@Resource
	private ContentDao contentDao;


	@Resource
	private RedisTemplate<String,Object> redisTemplate;

	@Override
	public List<Content> findAll() {
		List<Content> list = contentDao.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		Page<Content> page = (Page<Content>)contentDao.selectByExample(null);
		return new PageResult(page.getTotal(), page.getResult());
	}

	@Transactional
	@Override
	public void add(Content content) {
		// 更新缓存
		clearCache(content.getCategoryId());
		// 新增广告
		contentDao.insertSelective(content);
	}

	// 增删改广告时更新缓存
	private void clearCache(Long categoryId) {
		redisTemplate.boundHashOps("content").delete(categoryId);
	}

	@Transactional
	@Override
	public void edit(Content content) {
		Long newCategoryId = content.getCategoryId(); // 新分类id
		Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId(); // 旧分类id
		// 更新缓存
		if(newCategoryId != oldCategoryId){ // 判断:分类改变时
			clearCache(newCategoryId);
			clearCache(oldCategoryId);
		}else{
			clearCache(newCategoryId);
		}
		// 更新广告
		contentDao.updateByPrimaryKeySelective(content);
	}

	@Override
	public Content findOne(Long id) {
		Content content = contentDao.selectByPrimaryKey(id);
		return content;
	}

	@Transactional
	@Override
	public void delAll(Long[] ids) {
		if(ids != null){
			for(Long id : ids){
				// 清除缓存
				clearCache(contentDao.selectByPrimaryKey(id).getCategoryId());
				contentDao.deleteByPrimaryKey(id);
			}
		}
	}

    @Override
    public List<Content> findByCategoryId(Long categoryId) {
		// 首先判断缓存中是否有数据
		List<Content> list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
		if(list == null){ // 缓存中没有数据,从数据库中查询
			// 预防高并发,同时访问数据库,加锁机制
			synchronized (this){
				// 为提升效率: 双层锁机制
				list = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
				if(list == null){
					ContentQuery query = new ContentQuery();
					// 可用的广告:status=1
					query.createCriteria().andCategoryIdEqualTo(categoryId).andStatusEqualTo("1");
					query.setOrderByClause("sort_order desc"); // 根据该字段排序
					list = contentDao.selectByExample(query);
					// 将数据放入缓存中
					redisTemplate.boundHashOps("list").put(categoryId, list);

				}
			}
		}
		return list;
    }


}
