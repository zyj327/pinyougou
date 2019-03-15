package com.pyg.core.service.temp;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.core.dao.specification.SpecificationOptionDao;
import com.pyg.core.dao.template.TypeTemplateDao;
import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.specification.SpecificationOption;
import com.pyg.core.pojo.specification.SpecificationOptionQuery;
import com.pyg.core.pojo.template.TypeTemplate;
import com.pyg.core.pojo.template.TypeTemplateQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;
    @Resource
    private RedisTemplate<String,Object> redisTemplate;

    /**
     * 条件查询所有模板
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {
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
        // 设置分页参数
        PageHelper.startPage(page,rows);
        // 设置查询条件
        TypeTemplateQuery query = new TypeTemplateQuery();
        if(typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())){
            query.createCriteria().andNameLike("%"+typeTemplate.getName().trim()+"%");
        }
        // 根据id降序
        query.setOrderByClause("id desc");
        // 封装结果集
        Page<TypeTemplate> page1 = (Page<TypeTemplate>) typeTemplateDao.selectByExample(query);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Transactional
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 根据id查询个模板:模板修改之数据回显
     * @param id
     */
    @Override
    public TypeTemplate findOne(Long id) {
        return typeTemplateDao.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void update(TypeTemplate typeTemplate) {
        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    @Transactional
    @Override
    public void delete(Long[] ids) {

        typeTemplateDao.deleteByPrimaryKeys(ids);
    }

    @Transactional
    @Override
    public void delOne(Long id) {
        typeTemplateDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<TypeTemplate> selectOptionList() {
        return typeTemplateDao.selectOptionList();
    }

    @Override
    public List<Map> findBySpecList(Long id) {
        // 根据id获取对象
        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);
        // 获取规格
        String specIds = typeTemplate.getSpecIds();
        // 转换对象
        List<Map> list = JSON.parseArray(specIds, Map.class);
        for (Map map : list) {
            // 获取规格id
            long specId = Long.parseLong(map.get("id").toString());
            // 根据id获取对应规格选项
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(specId);
            List<SpecificationOption> options = specificationOptionDao.selectByExample(query);
            map.put("options",options);
        }
        return list;
    }
}
