package com.yyj.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Goods {
    private Long id;
    private String goodsName;
    private String goodsTitle;
    private String goodsImg;
    private String goodsDetail;
    private Double goodsPrice;
    private Integer goodsStock;
}
