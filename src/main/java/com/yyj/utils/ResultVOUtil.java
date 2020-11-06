package com.yyj.utils;

import com.yyj.enums.StatusEnum;
import com.yyj.vo.ResultVO;

public class ResultVOUtil {
    public static ResultVO success(Object object){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(0);
        resultVO.setMsg("成功");
        resultVO.setData(object);
        return resultVO;
    }

    public static ResultVO success(){
        return success(null);
    }

    public static ResultVO error(StatusEnum statusEnum){
        ResultVO resultVO=new ResultVO();
        resultVO.setCode(statusEnum.getCode());
        resultVO.setMsg(statusEnum.getMsg());
        return resultVO;
    }
}
