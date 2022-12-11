package com.example.common.validator.Constraints;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;

/**
 * @author zp
 * @date 2022/12/10
 * @apiNote
 */
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    private Set<Integer> listValue = new HashSet<>();
    /**
     * 初始化
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ListValue constraintAnnotation) {
//        ConstraintValidator.super.initialize(constraintAnnotation);
        int[] value = constraintAnnotation.value();
        for (int var : value) {
            listValue.add(var);
        }

    }

    /**
     * 校验的值
     * @param value object to validate
     * @param context context in which the constraint is evaluated
     *
     * @return
     */
    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        return listValue.contains(value);
    }
}
