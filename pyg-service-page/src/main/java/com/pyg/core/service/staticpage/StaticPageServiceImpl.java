package com.pyg.core.service.staticpage;

import com.pyg.core.dao.good.GoodsDao;
import com.pyg.core.dao.good.GoodsDescDao;
import com.pyg.core.dao.item.ItemCatDao;
import com.pyg.core.dao.item.ItemDao;
import com.pyg.core.pojo.good.Goods;
import com.pyg.core.pojo.good.GoodsDesc;
import com.pyg.core.pojo.item.Item;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticPageServiceImpl implements StaticPageService,ServletContextAware {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private ItemDao itemDao;

    // springmvc支持FreeMarkerConfigurer的
    // 注入FreeMarkerConfigurer: 获取需要的Configuration并且指定模板位置
    private Configuration configuration;

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    private ServletContext servletContext;

    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 商品审核:商品详情生成静态页
     * @param id
     */
    @Override
    public void getHtml(Long id) {
        try {
            // 1,创建Configuration并制定模板位置

            // 2,通过Configuration获取模板
            Template template = configuration.getTemplate("item.ftl");
            // 3,准备数据
            Map<String,Object> map = getDpataModel(id);
            String pathname = "/" + id + ".html";
            String path = servletContext.getRealPath(pathname);
            File file = new File(path);
            // 4,模板+数据=输出
            template.process(map,new OutputStreamWriter(new FileOutputStream(file),"UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 静态页需要的数据
     * @param id
     * @return
     */
    private Map<String,Object> getDpataModel(Long id) {
        Map<String,Object> map = new HashMap<>();
        // 1,商品副标题
        Goods goods = goodsDao.selectByPrimaryKey(id);
        map.put("goods",goods);
        // 2,商品图片,介绍等
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        map.put("goodsDesc",goodsDesc);
        // 3,商品库存
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1").andNumGreaterThan(0);
        List<Item> itemList = itemDao.selectByExample(query);
        map.put("itemList",itemList);
        // 4,商品分类
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
        map.put("itemCat1",itemCat1);
        map.put("itemCat2",itemCat2);
        map.put("itemCat3",itemCat3);
        return map;
    }
}
