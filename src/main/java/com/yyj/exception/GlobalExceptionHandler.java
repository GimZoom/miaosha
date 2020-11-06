package com.yyj.exception;

import com.yyj.enums.StatusEnum;
import com.yyj.utils.ResultVOUtil;
import com.yyj.vo.ResultVO;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@ControllerAdvice
@ResponseBody
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResultVO exceptionHandler(Exception e) {
        e.printStackTrace();
        if (e instanceof GlobalException) {
            GlobalException globalException = (GlobalException) e;
            return ResultVOUtil.error(globalException.getStatusEnum());
        } else if (e instanceof BindException) {
            BindException bindException = (BindException) e;
            List<ObjectError> allErrors = bindException.getAllErrors();
            String message = allErrors.get(0).getDefaultMessage();
            return ResultVOUtil.error(StatusEnum.BIND_ERROR.fillArgs(message));
        } else {
            return ResultVOUtil.error(StatusEnum.SERVER_ERROR);
        }
    }
}
