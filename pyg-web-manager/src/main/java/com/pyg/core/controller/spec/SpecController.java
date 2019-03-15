package com.pyg.core.controller.spec;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.specification.Specification;
import com.pyg.core.service.spec.SpecService;
import com.pyg.core.vo.SpecVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/specification")
public class SpecController {

    @Reference
    private SpecService specService;

    /**
     * 条件查询所有规格
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification){
        return specService.search(page, rows, specification);
    }

    /**
     * 規格添加
     * @param specVo
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody SpecVo specVo){
        try {
            specService.add(specVo);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 修改規格之数据回显
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public SpecVo findOne(Long id){
        return specService.findOne(id);
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            specService.delete(ids);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 修改规格
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody SpecVo specVo){
        try {
            specService.update(specVo);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 删除单条数据
     * @param id
     * @return
     */
    @RequestMapping("/delOne.do")
    public Result delOne(Long id){
        try {
            specService.delOne(id);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 新增模板时加载的规格结果集
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        return specService.selectOptionList();
    }
}
