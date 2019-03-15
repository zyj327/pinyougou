package com.pyg.core.controller.brand;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.core.entity.PageResult;
import com.pyg.core.entity.Result;
import com.pyg.core.pojo.good.Brand;
import com.pyg.core.service.brand.BrandService;
import org.apache.ibatis.annotations.Delete;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有品牌
     * @return
     */
    @RequestMapping("/findAll.do")
    public List<Brand> findAll(){
        return brandService.findAll();
    }

    /**
     * 条件查询所有品牌
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody Brand brand){
        return brandService.search(pageNum,pageSize,brand);
    }

    /**
     * 添加品牌
     * @param brand
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody Brand brand){
        try {
            brandService.add(brand);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 根据id查询单条数据
     * @param id
     * @return
     */
    @RequestMapping("/findOne.do")
    public Brand findOne(Long id){
        return brandService.findOne(id);
    }

    /**
     * 修改品牌数据
     * @param brand
     * @return
     */
    @RequestMapping("/update.do")
    public Result update(@RequestBody Brand brand){
        try {
            brandService.update(brand);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 删除单个品牌
     * @param id
     * @return
     */
    @RequestMapping("/delOne.do")
    public Result delOne(Long id){
        try {
            brandService.delOne(id);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("/delete.do")
    public Result delete(Long[] ids){
        try {
            brandService.delete(ids);
            return new Result(true,"操作成功!");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"操作失败!");
        }
    }

    /**
     * 新增模板时加载品牌结果集
     * @return
     */
    @RequestMapping("/selectOptionList.do")
    public List<Map> selectOptionList(){
        return brandService.selectOptionList();
    }

}
