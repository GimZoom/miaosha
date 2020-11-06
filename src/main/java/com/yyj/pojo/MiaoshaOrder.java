package com.yyj.pojo;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class MiaoshaOrder {
    private Long id;
    private Long userId;
    private Long  orderId;
    private Long goodsId;
}
