package com.yyj.dao;

import com.yyj.pojo.MiaoshaGoods;
import com.yyj.vo.GoodsVO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface GoodsDao {
    /**
     * 查询所有秒杀商品
     *
     * @return
     */
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id=g.id")
    List<GoodsVO> findGoodsVO();


    /**
     * 根据商品Id查询
     *
     * @param goodsId
     * @return
     */
    @Select("select g.*,mg.miaosha_price,mg.stock_count,mg.start_date,mg.end_date from miaosha_goods mg left join goods g on mg.goods_id=g.id " +
            "where g.id=#{goodsId}")
    GoodsVO findGoodsVOByGoodsId(@Param("goodsId") Long goodsId);

    /**
     * 秒杀商品的库存-1
     *
     * @param miaoshaGoods
     */
    @Update("update miaosha_goods set stock_count=stock_count-1 where goods_id=#{goodsId} and stock_count>0")
    Integer reduceStock(MiaoshaGoods miaoshaGoods);

    /**
     * 重置秒杀商品的库存
     *
     * @param miaoshaGoods
     */
    @Update("update miaosha_goods set stock_count=#{stockCount} where goods_id=#{goodsId}")
    Integer resetStock(MiaoshaGoods miaoshaGoods);
}
