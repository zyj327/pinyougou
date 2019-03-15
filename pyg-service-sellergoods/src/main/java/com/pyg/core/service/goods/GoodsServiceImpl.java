package com.pyg.core.service.goods;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.core.dao.good.BrandDao;
import com.pyg.core.dao.good.GoodsDao;
import com.pyg.core.dao.good.GoodsDescDao;
import com.pyg.core.dao.item.ItemCatDao;
import com.pyg.core.dao.item.ItemDao;
import com.pyg.core.dao.seller.SellerDao;
import com.pyg.core.entity.PageResult;
import com.pyg.core.pojo.good.Goods;
import com.pyg.core.pojo.good.GoodsDesc;
import com.pyg.core.pojo.good.GoodsQuery;
import com.pyg.core.pojo.item.Item;
import com.pyg.core.pojo.item.ItemCat;
import com.pyg.core.pojo.item.ItemQuery;
import com.pyg.core.service.staticpage.StaticPageService;
import com.pyg.core.vo.GoodsVo;
import org.opensaml.xml.signature.Q;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDao goodsDao;
    @Resource
    private GoodsDescDao goodsDescDao;
    @Resource
    private ItemDao itemDao;
    @Resource
    private ItemCatDao itemCatDao;
    @Resource
    private BrandDao brandDao;
    @Resource
    private SellerDao sellerDao;
    @Resource
    private SolrTemplate solrTemplate;
//    @Resource
//    private StaticPageService staticPageService;
    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private Destination topicPageAndSolrDestination;
    @Resource
    private Destination queueSolrDeleteDestination;

    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {
        // 1,添加商品信息,并返回自增主键
        Goods goods = goodsVo.getGoods();
        // 设置商品默认状态:未审核
        goods.setAuditStatus("0");
        goodsDao.insertSelective(goods);
        // 2,添加商品详细信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        // 设置外键
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);
        // 3,添加商品规格列表
        // 判断是否启用规格
        if("1".equals(goods.getIsEnableSpec())){ // 启用规格:一个商品对应多个规格
            // 获取商品规格
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                // 设置库存的商品标题=spu名称+spu副标题+规格选项名称
                String title = goods.getGoodsName() + " " + goods.getCaption();
                String spec = item.getSpec();
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title+=" "+entry.getValue();
                }
                item.setTitle(title);
                setAttributeForItem(item,goods,goodsDesc);
                // 保存库存
                itemDao.insertSelective(item);
            }
        }else{ // 不启用规格:一个商品对应一个规格
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption()); // 库存商品标题
            item.setPrice(goods.getPrice()); //商品价格
            item.setIsDefault("1"); //默认的商品
            item.setSpec("{}"); //无规格
            item.setNum(9999);  //库存量
            setAttributeForItem(item,goods,goodsDesc);
            // 保存库存
            itemDao.insertSelective(item);
        }
    }

    @Override
    public PageResult search(Integer page, Integer rows, Goods goods) {
        // 设置分页条件
        PageHelper.startPage(page,rows);
        // 设置商家id
        GoodsQuery query = new GoodsQuery();
        if(goods.getSellerId() != null && !"".equals(goods.getSellerId())){
            query.createCriteria().andSellerIdEqualTo(goods.getSellerId());
        }
        // 降序排序
        query.setOrderByClause("id desc");
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public PageResult searchByManager(Integer page, Integer rows, Goods goods) {
        // 设置分页条件
        PageHelper.startPage(page,rows);
        // 设置查询条件: 未审核,未删除
        GoodsQuery query = new GoodsQuery();
        GoodsQuery.Criteria criteria = query.createCriteria();
        if(goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus().trim())){
            // 未审核
            criteria.andAuditStatusEqualTo(goods.getAuditStatus().trim());
        }
        // 未删除:逻辑删除is_delete != null
        criteria.andIsDeleteIsNull();
        // 降序排序
        query.setOrderByClause("id desc");
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(query);
        return new PageResult(p.getTotal(),p.getResult());
    }

    @Override
    public GoodsVo findOne(Long id) {
        GoodsVo goodsVo = new GoodsVo();
        // 1,查询商品基本信息
        Goods goods = goodsDao.selectByPrimaryKey(id);
        goodsVo.setGoods(goods);
        // 2,查询商品描详细信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);
        // 3,查询商品库存信息: 根据库存中商品id查询(goods_id)
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id);
        List<Item> items = itemDao.selectByExample(query);
        goodsVo.setItemList(items);
        return goodsVo;

    }

    /**
     * 更新商品
     * @param goodsVo
     */
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) {
        // 1,更新商品基本信息
        Goods goods = goodsVo.getGoods();
        // 修改商品重新审核
        goods.setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goods);
        // 2,更新商品描述信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);
        // 3,更新商品库存信息(先清空再添加)
        // 3.1 清空库存
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(goods.getId());
        itemDao.deleteByExample(query);
        // 3.2 再添加库存信息
        // 判断是否启用规格
        if("1".equals(goods.getIsEnableSpec())){ // 启用规格:一个商品对应多个规格
            // 获取商品规格
            List<Item> itemList = goodsVo.getItemList();
            for (Item item : itemList) {
                // 设置库存的商品标题=spu名称+spu副标题+规格选项名称
                String title = goods.getGoodsName() + " " + goods.getCaption();
                String spec = item.getSpec();
                Map<String,String> map = JSON.parseObject(spec, Map.class);
                Set<Map.Entry<String, String>> entries = map.entrySet();
                for (Map.Entry<String, String> entry : entries) {
                    title+=" "+entry.getValue();
                }
                item.setTitle(title);
                setAttributeForItem(item,goods,goodsDesc);
                // 保存库存
                itemDao.insertSelective(item);
            }
        }else{ // 不启用规格:一个商品对应一个规格
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption()); // 库存商品标题
            item.setPrice(goods.getPrice()); //商品价格
            item.setIsDefault("1"); //默认的商品
            item.setSpec("{}"); //无规格
            item.setNum(9999);  //库存量
            setAttributeForItem(item,goods,goodsDesc);
            // 保存库存
            itemDao.insertSelective(item);
        }
    }

    /**
     * 审核商品
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {
        // 设置审核状态
        Goods goods = new Goods();
        goods.setAuditStatus(status);
        for (final Long id : ids) {
            goods.setId(id);
            // 更新商品
            goodsDao.updateByPrimaryKeySelective(goods);
            if("1".equals(status)){
                // 将商品信息保存到索引库中（上架）
                //dataImportToSolr(); 所有商品保存到索引库
                //isShow(id);// 审核通过的商品保存到索引库
                // 生成商品详情的静态页
                //staticPageService.getHtml(id);

                // 将消息(商品id) 发送到mq中
                jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {
                        // 将商品id封装成消息体: 文本消息,map消息
                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                        return textMessage;
                    }
                });

            }
        }
    }

    /**
     * 将审核通过的商品添加到索引库
     * @param id
     */
    private void isShow(Long id) {
        // 将该商品对应的库存中价格最低的sku保存到索引库中
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1")
                .andIsDefaultEqualTo("1").andNumGreaterThan(0);
        List<Item> items = itemDao.selectByExample(itemQuery);
        if(items != null && items.size()>0){ //判断items不为空
            for (Item item : items) {
                // 处理动态字段
                String spec = item.getSpec();
                Map<String,String> specMap = JSON.parseObject(spec, Map.class);
                item.setSpecMap(specMap);
            }
            // 将数据保存至索引库
            solrTemplate.saveBeans(items);
            // 提交操作
            solrTemplate.commit();

        }
    }

    // 将数据库数据导入索引库
    private void dataImportToSolr() {
        // 查询所有sku
        ItemQuery query = new ItemQuery();
        query.createCriteria().andStatusEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        if(items != null && items.size()>0){ //判断items不为空
            for (Item item : items) {
                // 处理动态字段
                String spec = item.getSpec();
                Map<String,String> specMap = JSON.parseObject(spec, Map.class);
                item.setSpecMap(specMap);
            }
            // 将数据保存至索引库
            solrTemplate.saveBeans(items);
            // 提交操作
            solrTemplate.commit();

        }
    }

    @Transactional
    @Override
    public void delete(Long[] ids) {
        Goods goods = new Goods();
        goods.setIsDelete("1"); // 1,逻辑删除, null不删除
        for (final Long id : ids) {
            goods.setId(id);
            goodsDao.updateByPrimaryKeySelective(goods);
            // 删除索引库中的商品（下架）
            // SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
            // solrTemplate.delete(query);
            // solrTemplate.commit();
            // 删除该商品详情静态页【可选】
            // 将商品id发送到mq中
            jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                @Override
                public Message createMessage(Session session) throws JMSException {
                    // 封装消息体
                    TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                    return textMessage;
                }
            });
        }
    }

    @Transactional
    @Override
    public void delOne(final Long id) {
        Goods goods = new Goods();
        goods.setIsDelete("1");
        goods.setId(id);
        goodsDao.updateByPrimaryKeySelective(goods);
        // 删除索引库中的商品（下架）
        SimpleQuery query = new SimpleQuery("item_goodsid:"+id);
        // 删除该商品详情静态页【可选】
        // 将商品id发送到mq中
        jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                // 封装消息体
                TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                return textMessage;
            }
        });
    }

    /**
     * 设置item的公共属性
     */
    public void setAttributeForItem(Item item,Goods goods,GoodsDesc goodsDesc) {
        // 设置库存的图片:一张图片
        String itemImages = goodsDesc.getItemImages();
        List<Map> images = JSON.parseArray(itemImages, Map.class);
        if(images != null && images.size()>0){
            // 取第一张图
            String img = images.get(0).get("url").toString();
            item.setImage(img);
        }
        item.setCategoryid(goods.getCategory3Id()); //设置三级分类id
        item.setStatus("1"); // 库存状态: 1:正常
        item.setCreateTime(new Date()); //创建时间
        item.setUpdateTime(new Date()); //更新时间
        item.setGoodsId(goods.getId()); //商品id
        item.setSellerId(goods.getSellerId()); //商家id
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName()); // 分类名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName()); //品牌名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());//商家店铺名称
    }
}
