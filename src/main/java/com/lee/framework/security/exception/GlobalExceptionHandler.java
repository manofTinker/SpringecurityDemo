package com.lee.framework.security.exception;

import com.lee.framework.security.bean.SimpleResponse;
import com.lee.framework.security.common.BaseError;
import com.lee.framework.security.common.BaseErrorEnum;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * 处理{@link Exception}异常
     *
     * @return 返回统一json数据对象
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public SimpleResponse<Object> onException(Exception e) {
        if (e instanceof RestBusinessException) {
            RestBusinessException businessException = (RestBusinessException) e;
            BaseError baseError = businessException.getBaseError();
            return SimpleResponse.error(baseError.getCode(), baseError.getMessage(), false);
        } else if (e instanceof AccessDeniedException) {
            return SimpleResponse.error(BaseErrorEnum.REQUIRED_ROLE);
        } else if (e instanceof HttpRequestMethodNotSupportedException) {
            return SimpleResponse.error(BaseErrorEnum.REQUEST_NOT_SUPPORT);
        } else if (e instanceof NoHandlerFoundException) {
            return SimpleResponse.error(BaseErrorEnum.NO_HANDLER_EXCEPTION);
        } else if (e instanceof MissingServletRequestParameterException) {
            return SimpleResponse.error(BaseErrorEnum.PARAMETER_NOT_FOUND);
        } else if (e instanceof MaxUploadSizeExceededException) {
            return SimpleResponse.error(BaseErrorEnum.FILE_TOO_LARGE_EXCEPTION);
        } else {
            logger.error("请求发生错误：", e);
            return SimpleResponse.error(BaseErrorEnum.SERVER_ERROR);
        }
    }

}
