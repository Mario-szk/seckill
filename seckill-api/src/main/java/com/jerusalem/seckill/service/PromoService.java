package com.jerusalem.seckill.service;

import com.jerusalem.seckill.model.PromoModel;

/****
 * 活动接口
 * @author jerusalem
 * @date 2020-04-19 13:44:19
 */
public interface PromoService {

    /***
     * 根据itemid获取即将进行的或正在进行的秒杀活动
     * @param itemId
     * @return
     */
    PromoModel getPromoByItemId(Integer itemId);

    /***
     * 活动发布
     * @param promoId
     */
    void publishPromo(Integer promoId);

    /***
     * 生成秒杀用的令牌
     * @param promoId
     * @param itemId
     * @param userId
     * @return
     */
    String generateSecondKillToken(Integer promoId, Integer itemId, Integer userId);
}
