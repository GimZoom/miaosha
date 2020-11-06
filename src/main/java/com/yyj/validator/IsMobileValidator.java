package com.yyj.validator;

import com.yyj.utils.ValidatorUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

//IsMobile：自定义的注解
//String：注解参数类型
public class IsMobileValidator implements ConstraintValidator<IsMobile, String> {
    //默认值_false，用于接收注解上自定义的 required
    private boolean required = false;

    //1、初始化方法：通过该方法我们可以拿到我们的注解
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required=constraintAnnotation.required();
    }

    //2、逻辑处理
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(required){
            return ValidatorUtil.isMobile(s);
        }else {
            if (StringUtils.isEmpty(s)){
                return true;
            }else {
                return ValidatorUtil.isMobile(s);
            }
        }
    }
}
