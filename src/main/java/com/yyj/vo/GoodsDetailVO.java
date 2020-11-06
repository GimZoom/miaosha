package com.yyj.vo;

import com.yyj.pojo.User;
import lombok.Data;

@Data
public class GoodsDetailVO {
    private Integer miaoshaStatus;
    private Integer remainSeconds;
    private GoodsVO goods;
    private User user;
}
