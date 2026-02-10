package com.jsdc.iotpt.util.exception;

import com.jsdc.iotpt.common.Constants;
import com.jsdc.iotpt.util.AjaxResult;
import com.jsdc.iotpt.util.HttpStatus;
import com.jsdc.iotpt.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import javax.security.auth.login.AccountExpiredException;
import java.nio.file.AccessDeniedException;
import java.sql.SQLException;

/**
 * 全局异常处理器
 *
 * @author gl
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
  static String errorMsg = Constants.errorMsg;

  /** 基础异常 */
  @ExceptionHandler(BaseException.class)
  public AjaxResult baseException(BaseException e) {
    return AjaxResult.error(errorMsg);
  }

  /** 业务异常 */
  @ExceptionHandler(CustomException.class)
  public AjaxResult businessException(CustomException e) {
    log.error(e.getMessage(), e);
    if (StringUtils.isNotBlank(e.getMessage())) {
      return AjaxResult.error(e.getMessage());
    }
    return AjaxResult.error(errorMsg);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  public AjaxResult handlerNoFoundException(Exception e) {
    log.error(e.getMessage(), e);
    return AjaxResult.error(HttpStatus.NOT_FOUND, errorMsg);
  }

  @ExceptionHandler(AccessDeniedException.class)
  public AjaxResult handleAuthorizationException(AccessDeniedException e) {
    log.error(e.getMessage());
    return AjaxResult.error(HttpStatus.FORBIDDEN, errorMsg);
  }

  @ExceptionHandler(AccountExpiredException.class)
  public AjaxResult handleAccountExpiredException(AccountExpiredException e) {
    log.error(e.getMessage(), e);
    return AjaxResult.error(errorMsg);
  }
//
//  @ExceptionHandler(UsernameNotFoundException.class)
//  public AjaxResult handleUsernameNotFoundException(UsernameNotFoundException e) {
//    log.error(e.getMessage(), e);
//    return AjaxResult.error(e.getMessage());
//  }

  @ExceptionHandler(DataAccessException.class)
  public AjaxResult handleDataAccessException(DataAccessException e) {
    log.error(e.getMessage(), e);
    return AjaxResult.error(errorMsg);
  }

  @ExceptionHandler(SQLException.class)
  public AjaxResult handleSQLException(SQLException e) {
    log.error(e.getMessage(), e);
    return AjaxResult.error(errorMsg);
  }

  @ExceptionHandler(Exception.class)
  public AjaxResult handleException(Exception e) {
    log.error(e.getMessage(), e);
    return AjaxResult.error(errorMsg);
  }

  /** 自定义验证异常 */
  @ExceptionHandler(BindException.class)
  public AjaxResult validatedBindException(BindException e) {
    log.error(e.getMessage(), e);
//    String message = e.getAllErrors().get(0).getDefaultMessage();
    return AjaxResult.error(errorMsg);
  }

  /** 自定义验证异常 */
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public Object validExceptionHandler(MethodArgumentNotValidException e) {
    log.error(e.getMessage(), e);
//    String message = e.getBindingResult().getFieldError().getDefaultMessage();
    return AjaxResult.error(errorMsg);
  }

  /** 演示模式异常 */
  @ExceptionHandler(DemoModeException.class)
  public AjaxResult demoModeException(DemoModeException e) {
    return AjaxResult.error("演示模式，不允许操作");
  }
}
