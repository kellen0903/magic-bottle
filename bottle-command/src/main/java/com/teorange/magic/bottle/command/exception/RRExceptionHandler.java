//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.teorange.magic.bottle.command.exception;

import cn.teorange.framework.core.exception.RRException;
import cn.teorange.framework.core.utils.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RRExceptionHandler {


  public RRExceptionHandler() {
  }

  @ExceptionHandler({RRException.class})
  public R handleRRException(RRException e) {
    R r = new R();
    r.put("code", e.getCode());
    r.put("msg", e.getMessage());
    return r;
  }

  @ExceptionHandler({Exception.class})
  public R handleException(Exception e) {
    log.error(e.getMessage(), e);
    return R.error();
  }
}
