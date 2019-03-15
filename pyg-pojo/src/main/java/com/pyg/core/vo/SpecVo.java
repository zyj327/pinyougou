package com.pyg.core.vo;

import com.pyg.core.pojo.specification.Specification;
import com.pyg.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * Vo:value object,值对象（封装页面的数据的）
 */
public class SpecVo implements Serializable {
    private Specification specification;
    private List<SpecificationOption> specificationOptionList;

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
