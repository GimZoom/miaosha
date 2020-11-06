package com.yyj.service.impl;

import com.yyj.dao.GoodsDao;
import com.yyj.pojo.MiaoshaGoods;
import com.yyj.service.GoodsService;
import com.yyj.vo.GoodsVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoodsServiceImpl implements GoodsService {
    @Autowired
    private GoodsDao goodsDao;

    /**
     * 查询所有GoodsVO
     *
     * @return
     */
    @Override
    public List<GoodsVO> findGoodsVO() {
        return goodsDao.findGoodsVO();
    }

    /**
     * 根据GoodsId查询GoodsVO
     *
     * @param goodsId
     * @return
     */
    @Override
    public GoodsVO findGoodsVOByGoodsId(Long goodsId) {
        return goodsDao.findGoodsVOByGoodsId(goodsId);
    }

    /**
     * 减少秒杀商品的库存
     *
     * @param goodsVO
     */
    @Override
    public Boolean reduceStock(GoodsVO goodsVO) {
        MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
        miaoshaGoods.setGoodsId(goodsVO.getId());
        Integer result = goodsDao.reduceStock(miaoshaGoods);
        return result > 0;
    }

    /**
     * 重置商品库存
     *
     * @param goodsVOList
     */
    @Override
    public void resetStock(List<GoodsVO> goodsVOList) {
        for (GoodsVO goods : goodsVOList) {
            MiaoshaGoods miaoshaGoods = new MiaoshaGoods();
            miaoshaGoods.setGoodsId(goods.getId());
            miaoshaGoods.setStockCount(goods.getStockCount());
            goodsDao.resetStock(miaoshaGoods);
        }
    }
}
