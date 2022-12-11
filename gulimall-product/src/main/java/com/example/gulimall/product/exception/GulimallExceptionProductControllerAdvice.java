package com.example.gulimall.product.exception;

import com.example.common.exception.BIZException;
import com.example.common.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zp
 * @date 2022/12/10
 * @apiNote
 */
@RestControllerAdvice(basePackages = "com.example.gulimall.product.controller")
@Slf4j
public class GulimallExceptionProductControllerAdvice {

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public R validatorException(MethodArgumentNotValidException exception, HttpServletRequest httpServletRequest) {
        log.error("MethodArgumentNotValidException: {}", exception.getMessage());

        Map<String, String> errorsMap = new HashMap<>();
        if (exception.hasErrors()) {
            List<FieldError> fieldErrors = exception.getFieldErrors();
            fieldErrors.forEach(t -> {
                errorsMap.put(t.getField(), t.getDefaultMessage());
            });
        }
        return R.error(BIZException.VALIDATOR_EXCEPTION).put("path", httpServletRequest.getServletPath()).put(errorsMap);
    }

    @ExceptionHandler(Throwable.class)
    public R handlerThrowException(Throwable throwable, HttpServletRequest httpServletRequest) {
        log.error("Throwable异常: ", throwable);
        return R.error().put("path", httpServletRequest.getServletPath());
    }
}
