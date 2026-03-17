package com.frontleaves.phalanx.beacon.sso.sdk.springboot.exception;

import com.xlf.utility.BaseResponse;
import com.xlf.utility.ErrorCode;
import com.xlf.utility.mvc.ResultUtil;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * <p>
 * 统一处理 Jakarta Validation 校验异常，将校验失败信息转换为标准的 {@link BaseResponse} 格式。
 * 处理的异常类型包括：
 * <ul>
 *   <li>{@link MethodArgumentNotValidException} - 处理 {@code @Valid} 注解触发的请求体校验异常</li>
 *   <li>{@link ConstraintViolationException} - 处理方法级别参数校验异常</li>
 * </ul>
 * </p>
 *
 * @author xiao_lfeng
 * @since 0.0.1
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理请求体校验异常
     * <p>
     * 当使用 {@code @Valid} 注解校验 {@code @RequestBody} 参数时，
     * 如果校验失败将触发此异常。提取第一个校验错误字段及其消息返回给客户端。
     * </p>
     *
     * @param ex 方法参数校验异常
     * @return 标准错误响应，包含校验失败的字段和错误信息
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BaseResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex
    ) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String message = fieldError != null ? fieldError.getDefaultMessage() : "请求参数校验失败";
        log.warn("请求参数校验失败: {}", message);
        return ResultUtil.error(ErrorCode.PARAMETER_MISSING, message, null);
    }

    /**
     * 处理约束违反异常
     * <p>
     * 当方法级别的约束校验（如 {@code @Validated} 在类或方法上使用时）失败时触发。
     * 将所有违反的约束信息合并返回。
     * </p>
     *
     * @param ex 约束违反异常
     * @return 标准错误响应，包含所有校验失败信息
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<BaseResponse<Void>> handleConstraintViolationException(
            ConstraintViolationException ex
    ) {
        String message = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("约束校验失败: {}", message);
        return ResultUtil.error(ErrorCode.PARAMETER_MISSING, message, null);
    }
}
