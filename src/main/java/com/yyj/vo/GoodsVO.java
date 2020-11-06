package com.yyj.vo;

import com.yyj.pojo.Goods;
import lombok.Data;

import java.util.Date;

/**
 * 商品和秒杀商品集成实体
 */
@Data
public class GoodsVO extends Goods {
    private Double miaoshaPrice;
    private Integer stockCount;
    private Date startDate;
    private Date endDate;
}
