package com.yyj.dao;

import com.yyj.pojo.MiaoshaOrder;
import com.yyj.pojo.OrderInfo;
import org.apache.ibatis.annotations.*;

public interface OrderDao {
    /**
     * 通过Id查询订单
     *
     * @param orderId
     * @return
     */
    @Select("select * from order_info where id=#{orderId}")
    OrderInfo findOrderInfoById(@Param("orderId") Long orderId);

    /**
     * 通过UserId和GoodsId查询秒杀订单
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("select * from order_info where user_id=#{userId} and goods_id=#{goodsId}")
    OrderInfo findOrderInfoByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    /**
     * 通过UserId和GoodsId查询秒杀订单
     *
     * @param userId
     * @param goodsId
     * @return
     */
    @Select("select * from miaosha_order where user_id=#{userId} and goods_id=#{goodsId}")
    MiaoshaOrder findMiaoshaOrderByUserIdAndGoodsId(@Param("userId") Long userId, @Param("goodsId") Long goodsId);

    /**
     * 插入OrderInfo
     *
     * @param orderInfo
     * @return
     */
    @Insert("insert into order_info (user_id,goods_id,goods_name,goods_count,goods_price,order_channel,status,create_date) " +
            "values (#{userId},#{goodsId}, #{goodsName}, #{goodsCount}, #{goodsPrice}, #{orderChannel},#{status},#{createDate})")
    @SelectKey(statement = "select last_insert_id()", keyProperty = "id", keyColumn = "id", before = false, resultType = Long.class)
    void insertOrderInfo(OrderInfo orderInfo);

    /**
     * 插入MiaoshaOrder
     *
     * @param miaoshaOrder
     * @return
     */
    @Insert("insert into miaosha_order (user_id, goods_id, order_id)values(#{userId}, #{goodsId}, #{orderId})")
    void insertMiaoshaOrder(MiaoshaOrder miaoshaOrder);

    /**
     * 清空订单详情表
     */
    @Delete("delete from order_info")
    void deleteOrders();

    /**
     * 清空秒杀订单表
     */
    @Delete("delete from miaosha_order")
    void deleteMiaoshaOrders();
}
