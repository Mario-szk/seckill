package com.jerusalem.seckill.service.impl;

import com.jerusalem.seckill.dao.ItemDOMapper;
import com.jerusalem.seckill.dao.StockLogDOMapper;
import com.jerusalem.seckill.dataobject.ItemDO;
import com.jerusalem.seckill.dataobject.ItemStockDO;
import com.jerusalem.seckill.dataobject.StockLogDO;
import com.jerusalem.seckill.error.BusinessException;
import com.jerusalem.seckill.error.EmBusinessError;
import com.jerusalem.seckill.rocketmq.MqProducer;
import com.jerusalem.seckill.model.ItemModel;
import com.jerusalem.seckill.model.PromoModel;
import com.jerusalem.seckill.utils.ValidatorImpl;
import com.jerusalem.seckill.dao.ItemStockDOMapper;
import com.jerusalem.seckill.service.ItemService;
import com.jerusalem.seckill.service.PromoService;
import com.jerusalem.seckill.utils.ValidationResult;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/****
 * 商品接口实现
 * @author jerusalem
 * @date 2020-04-20 15:30:29
 */
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private MqProducer mqProducer;

    @Autowired
    private ValidatorImpl validator;

    @Autowired
    private ItemDOMapper itemDOMapper;

    @Autowired
    private PromoService promoService;

    @Autowired
    private ItemStockDOMapper itemStockDOMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private StockLogDOMapper stockLogDOMapper;

    /***
     * 将商品模型转换为商品实体
     * @param itemModel
     * @return
     */
    private ItemDO convertItemDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemDO itemDO = new ItemDO();
        BeanUtils.copyProperties(itemModel,itemDO);
        itemDO.setPrice(itemModel.getPrice().doubleValue());
        return itemDO;
    }

    /***
     * 将商品模型转换为商品库存关联实体
     * @param itemModel
     * @return
     */
    private ItemStockDO convertItemStockDOFromItemModel(ItemModel itemModel){
        if(itemModel == null){
            return null;
        }
        ItemStockDO itemStockDO = new ItemStockDO();
        itemStockDO.setItemId(itemModel.getId());
        itemStockDO.setStock(itemModel.getStock());
        return itemStockDO;
    }

    /***
     * 创建商品项
     * @param itemModel
     * @return
     * @throws BusinessException
     */
    @Override
    @Transactional
    public ItemModel createItem(ItemModel itemModel) throws BusinessException {
        //校验入参
        ValidationResult result = validator.validate(itemModel);
        if(result.isHasErrors()){
            throw new BusinessException(EmBusinessError.PARAMETER_VALIDATION_ERROR,result.getErrMsg());
        }
        //转化itemmodel->dataobject
        ItemDO itemDO = this.convertItemDOFromItemModel(itemModel);
        //写入数据库
        itemDOMapper.insertSelective(itemDO);
        itemModel.setId(itemDO.getId());
        ItemStockDO itemStockDO = this.convertItemStockDOFromItemModel(itemModel);
        itemStockDOMapper.insertSelective(itemStockDO);
        //返回创建完成的对象
        return this.getItemById(itemModel.getId());
    }

    /***
     * 展示商品列表
     * @return
     */
    @Override
    public List<ItemModel> listItem() {
        List<ItemDO> itemDOList = itemDOMapper.listItem();
        List<ItemModel> itemModelList =  itemDOList.stream().map(itemDO -> {
            ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
            ItemModel itemModel = this.convertModelFromDataObject(itemDO,itemStockDO);
            return itemModel;
        }).collect(Collectors.toList());
        return itemModelList;
    }

    /***
     * 根据id获取商品详情
     * @param id
     * @return
     */
    @Override
    public ItemModel getItemById(Integer id) {
        ItemDO itemDO = itemDOMapper.selectByPrimaryKey(id);
        if(itemDO == null){
            return null;
        }
        //操作获得库存数量
        ItemStockDO itemStockDO = itemStockDOMapper.selectByItemId(itemDO.getId());
        //将dataobject->model
        ItemModel itemModel = convertModelFromDataObject(itemDO,itemStockDO);
        //获取活动商品信息
        PromoModel promoModel = promoService.getPromoByItemId(itemModel.getId());
        if(promoModel != null && promoModel.getStatus().intValue() != 3){
            itemModel.setPromoModel(promoModel);
        }
        return itemModel;
    }

    /***
     * 根据缓存key获取商品详情
     * @param id
     * @return
     */
    @Override
    public ItemModel getItemByIdInCache(Integer id) {
        ItemModel itemModel = (ItemModel) redisTemplate.opsForValue().get("item_validate_"+id);
        if(itemModel == null){
            itemModel = this.getItemById(id);
            redisTemplate.opsForValue().set("item_validate_"+id,itemModel);
            redisTemplate.expire("item_validate_"+id,10, TimeUnit.MINUTES);
        }
        return itemModel;
    }

    /***
     * 减库存
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    @Override
    @Transactional
    public boolean decreaseStock(Integer itemId, Integer amount) throws BusinessException {
        //int affectedRow =  itemStockDOMapper.decreaseStock(itemId,amount);
        long result = redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue() * -1);
        if(result >0){
            //更新库存成功
            return true;
        }else if(result == 0){
            //打上库存已售罄的标识
            redisTemplate.opsForValue().set("promo_item_stock_invalid_"+itemId,"true");
            //更新库存成功
            return true;
        }else{
            //更新库存失败
            increaseStock(itemId,amount);
            return false;
        }
    }

    /***
     * 增加库存
     * @param itemId
     * @param amount
     * @return
     * @throws BusinessException
     */
    @Override
    public boolean increaseStock(Integer itemId, Integer amount) throws BusinessException {
        redisTemplate.opsForValue().increment("promo_item_stock_"+itemId,amount.intValue());
        return true;
    }


    /**
     * 同步库存扣减消息
     */
    @Override
    public boolean asyncDecreaseStock(Integer itemId, Integer amount) {
        boolean mqResult = mqProducer.asyncReduceStock(itemId,amount);
        return mqResult;
    }

    /***
     * 增加销量
     * @param itemId
     * @param amount
     * @throws BusinessException
     */
    @Override
    @Transactional
    public void increaseSales(Integer itemId, Integer amount) throws BusinessException {
        itemDOMapper.increaseSales(itemId,amount);
    }

    /***
     * 初始化对应的库存流水
     * @param itemId
     * @param amount
     * @return
     */
    @Override
    @Transactional
    public String initStockLog(Integer itemId, Integer amount) {
        StockLogDO stockLogDO = new StockLogDO();
        stockLogDO.setItemId(itemId);
        stockLogDO.setAmount(amount);
        stockLogDO.setStockLogId(UUID.randomUUID().toString().replace("-",""));
        stockLogDO.setStatus(1);
        stockLogDOMapper.insertSelective(stockLogDO);
        return stockLogDO.getStockLogId();
    }

    /***
     * 将商品、库存实体转换为商品模型
     * @param itemDO
     * @param itemStockDO
     * @return
     */
    private ItemModel convertModelFromDataObject(ItemDO itemDO,ItemStockDO itemStockDO){
        ItemModel itemModel = new ItemModel();
        BeanUtils.copyProperties(itemDO,itemModel);
        itemModel.setPrice(new BigDecimal(itemDO.getPrice()));
        itemModel.setStock(itemStockDO.getStock());
        return itemModel;
    }
}
