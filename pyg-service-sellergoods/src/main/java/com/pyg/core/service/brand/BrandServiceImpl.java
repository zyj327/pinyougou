package com.pyg.core.service.brand;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.core.dao.good.BrandDao;
import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.good.Brand;
import com.pyg.core.pojo.good.BrandQuery;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    @Resource
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    @Override
    public PageResult search(Integer pagNum, Integer pageSize, Brand brand) {
        // 设置分页参数
        PageHelper.startPage(pagNum,pageSize);
        // 设置查询条件
        BrandQuery brandQuery = new BrandQuery();
        // 通过query封装查询条件
        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        if(brand.getName() != null && !"".equals(brand.getName().trim())){
            criteria.andNameLike("%"+brand.getName().trim()+"%");
        }
        if(brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())){
            criteria.andFirstCharEqualTo(brand.getFirstChar());
        }
        // 根据id排序
        brandQuery.setOrderByClause("id desc");
        // 根据条件查询结果集
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);
        // 结果集封装到PageResult对象中
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());
        return pageResult;
    }

    @Transactional
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    @Transactional
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKeySelective(brand);
    }

    @Transactional
    @Override
    public void delete(Long[] ids) {
        brandDao.deleteByPrimaryKeys(ids);
    }

    @Transactional
    @Override
    public void delOne(Long id) {
        brandDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<Map> selectOptionList() {
        return brandDao.selectOptionList();
    }
}
