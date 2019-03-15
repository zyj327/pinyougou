package com.pyg.core.service.spec;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.core.dao.specification.SpecificationDao;
import com.pyg.core.dao.specification.SpecificationOptionDao;
import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.specification.Specification;
import com.pyg.core.pojo.specification.SpecificationOption;
import com.pyg.core.pojo.specification.SpecificationOptionQuery;
import com.pyg.core.pojo.specification.SpecificationQuery;
import com.pyg.core.vo.SpecVo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SpecServiceImpl implements SpecService {

    @Resource
    private SpecificationDao specificationDao;
    @Resource
    private SpecificationOptionDao specificationOptionDao;

    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        // 设置分页参数
        PageHelper.startPage(page,rows);
        // 设置查询条件
        SpecificationQuery query = new SpecificationQuery();
        // 通过query封装查询条件
        SpecificationQuery.Criteria criteria = query.createCriteria();
        if(specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())){
            criteria.andSpecNameLike("%"+specification.getSpecName().trim()+"%");
        }
        // 根据id降序
        query.setOrderByClause("id desc");
        Page<Specification> page1 = (Page<Specification>) specificationDao.selectByExample(query);
        return new PageResult(page1.getTotal(),page1.getResult());
    }

    @Transactional
    @Override
    public void add(SpecVo specVo) {
        // 1,保存规格:保存成功后返回自增主键
        Specification specification = specVo.getSpecification();
        specificationDao.insertSelective(specification);
        // 2,保存规格选项
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();
        if(specificationOptionList != null && specificationOptionList.size()>0){
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId());
                // 逐条保存
                // specificationOptionDao.insertSelective(specificationOption);
            }
            // 批量保存
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    @Override
    public SpecVo findOne(Long id) {
        // 查询规格
        Specification specification = specificationDao.selectByPrimaryKey(id);
        // 查询规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = query.createCriteria();
        criteria.andSpecIdEqualTo(id);
        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(query);
        // 封装数据
        SpecVo specVo = new SpecVo();
        specVo.setSpecification(specification);
        specVo.setSpecificationOptionList(specificationOptionList);
        return specVo;
    }

    @Transactional
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            // 先删除规格选项
            SpecificationOptionQuery query = new SpecificationOptionQuery();
            query.createCriteria().andSpecIdEqualTo(id);
            specificationOptionDao.deleteByExample(query);
            // 再删除规格
            specificationDao.deleteByPrimaryKey(id);
        }

    }

    @Transactional
    @Override
    public void update(SpecVo specVo) {
        // 修改规格
        Specification specification = specVo.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);
        // 修改规格选项:先清空规格选择,再插入规格选项
        // 清空旧的规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(specification.getId());
        specificationOptionDao.deleteByExample(query);
        List<SpecificationOption> specificationOptionList = specVo.getSpecificationOptionList();
        // 保存新的规格选项
        if(specificationOptionList != null && specificationOptionList.size()>0){
            for (SpecificationOption specificationOption : specificationOptionList) {
                specificationOption.setSpecId(specification.getId());
                // 逐条保存
                // specificationOptionDao.insertSelective(specificationOption);
            }
            // 批量保存
            specificationOptionDao.insertSelectives(specificationOptionList);
        }
    }

    @Transactional
    @Override
    public void delOne(Long id) {
        // 先删除规格选项
        SpecificationOptionQuery query = new SpecificationOptionQuery();
        query.createCriteria().andSpecIdEqualTo(id);
        specificationOptionDao.deleteByExample(query);
        // 再删除规格
        specificationDao.deleteByPrimaryKey(id);
    }

    @Override
    public List<Map> selectOptionList() {
        return specificationDao.selectOptionList();
    }
}
