package com.yyj.service;

import com.yyj.vo.GoodsVO;

import java.util.List;

public interface GoodsService {
    List<GoodsVO> findGoodsVO();

    GoodsVO findGoodsVOByGoodsId(Long goodsId);

    Boolean reduceStock(GoodsVO goodsVO);

    void resetStock(List<GoodsVO> goodsVOList);
}
