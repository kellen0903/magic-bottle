package com.teorange.magic.bottle.command.utils;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.teorange.magic.bottle.api.service.MagicUserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import java.util.Date;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * jwt工具类
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017/9/21 22:21
 */
@ConfigurationProperties(prefix = "magic.jwt")
@Component
public class JwtUtils {

  private Logger logger = LoggerFactory.getLogger(getClass());

  private String secret;
  private long expire;
  private String header;

  @Autowired
  private MagicUserService magicUserService;

  /**
   * 生成jwt token
   */
  public String generateToken(String openId) {
    Date nowDate = new Date();
    //过期时间
    Date expireDate = new Date(nowDate.getTime() + expire * 1000);

    return Jwts.builder()
        .setHeaderParam("type", "JWT")
        .setSubject(openId)
        .setIssuedAt(nowDate)
        .setExpiration(expireDate)
        .signWith(SignatureAlgorithm.HS512, secret)
        .compact();
  }

  public Claims getClaimByToken(String token) {
    try {
      return Jwts.parser()
          .setSigningKey(secret)
          .parseClaimsJws(token)
          .getBody();
    } catch (Exception e) {
      logger.debug("validate is token error ", e);
      return null;
    }
  }

  /**
   * token是否过期
   *
   * @return true：过期
   */
  public boolean isTokenExpired(Date expiration) {
    return expiration.before(new Date());
  }


  /**
   * 是否需要刷新token
   */
  public boolean isRefresh(Date expiration) {
    long hour = DateUtil.between(expiration, new Date(), DateUnit.HOUR);
    return hour <= 24;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public long getExpire() {
    return expire;
  }

  public void setExpire(long expire) {
    this.expire = expire;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }
}
