package com.yyj.rabbitmq;

import com.yyj.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MiaoshaMessage {
    private User user;
    private Long goodsId;
}
