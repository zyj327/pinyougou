package com.pyg.core.service.item;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.core.dao.item.ItemCatDao;
import com.pyg.core.dao.template.TypeTemplateDao;
import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.pojo.item.ItemCatQuery;
import com.pyg.core.pojo.item.ItemQuery;
import com.pyg.core.pojo.template.TypeTemplate;
import com.pyg.core.vo.ItemVo;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private TypeTemplateDao typeTemplateDao;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     *商品分类
     * @param parentId
     * @return
     */
    @Override
    public List<ItemCat> findByParentId(Long parentId) {
        // 列表查询的过程中将数据同步到redis中
        List<ItemCat> itemCats = itemCatDao.selectByExample(null);
        if(itemCats != null && itemCats.size()>0){
            for (ItemCat itemCat : itemCats) {
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(),itemCat.getTypeId());
            }
        }
        // 设置查询条件
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        List<ItemCat> itemCats1 = itemCatDao.selectByExample(itemCatQuery);
        return itemCats1;
    }

    /**
     * 商品分类修改之数据回显
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);

    }

    /**
     * 添加商品分类
     * @param itemCat
     */
    @Transactional
    @Override
    public void add(ItemCat itemCat) {
        itemCatDao.insertSelective(itemCat);
    }

    /**
     * 条件查询所有商品分类
     * @param page
     * @param rows
     * @param itemCat
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, ItemCat itemCat) {
        // 设置分页参数
        PageHelper.startPage(page,rows);
        // 设置查询条件
        ItemCatQuery query = new ItemCatQuery();
        query.createCriteria().andParentIdEqualTo((long) 0);
        Page<ItemCat> p = (Page<ItemCat>) itemCatDao.selectByExample(query);
        return new PageResult(p.getTotal(),p.getResult());
    }

    /**
     * 修改商品分类
     * @param itemCat
     */
    @Transactional
    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    /**
     * 批量删除
     */
    @Transactional
    @Override
    public void dele(Long[] ids) {
        for (Long id : ids) {
            itemCatDao.deleteByPrimaryKey(id);
        }
    }

    /**
     * 查询所有分类列表
     * @return
     */
    @Override
    public List<ItemCat> findAll() {
        return itemCatDao.selectByExample(null);
    }



}
