package com.teorange.magic.bottle.command.utils;

import cn.hutool.http.HttpRequest;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * String字符串工具类
 *
 * @author cjj
 */
public class StringUtil {

  static Logger logger = LoggerFactory.getLogger(StringUtil.class);

  /**
   * 私有构造方法,将该工具类设为单例模式.
   */
  private StringUtil() {
  }

  /**
   * 函数功能说明 ： 判断字符串是否为空
   */
  public static boolean isEmpty(String str) {
    return null == str || "".equals(str);
  }

  /**
   * 函数功能说明 ： 判断对象数组是否为空
   */
  public static boolean isEmpty(Object[] obj) {
    return null == obj || 0 == obj.length;
  }

  /**
   * 判断对象是否为空
   */
  public static boolean isEmpty(Object obj) {
    if (null == obj) {
      return true;
    }
    if (obj instanceof String) {
      return ((String) obj).trim().isEmpty();
    }
    return !(obj instanceof Number) ? false : false;
  }

  /**
   * 函数功能说明 ： 判断集合是否为空
   */
  public static boolean isEmpty(List<?> obj) {
    return null == obj || obj.isEmpty();
  }

  /**
   * 函数功能说明 ： 判断Map集合是否为空
   */
  public static boolean isEmpty(Map<?, ?> obj) {
    return null == obj || obj.isEmpty();
  }

  /**
   * 函数功能说明 ： 获得文件名的后缀名
   */
  public static String getExt(String fileName) {
    return fileName.substring(fileName.lastIndexOf(".") + 1);
  }

  /**
   * 获取去掉横线的长度为32的UUID串.
   *
   * @return uuid.
   * @author WuShuicheng.
   */
  public static String get32UUID() {
    return UUID.randomUUID().toString().replace("-", "");
  }

  /**
   * 获取带横线的长度为36的UUID串.
   *
   * @return uuid.
   * @author WuShuicheng.
   */
  public static String get36UUID() {
    return UUID.randomUUID().toString();
  }

  /**
   * 验证一个字符串是否完全由纯数字组成的字符串，当字符串为空时也返回false.
   *
   * @param str 要判断的字符串 .
   * @return true or false .
   * @author WuShuicheng .
   */
  public static boolean isNumeric(String str) {
    if (StringUtils.isBlank(str)) {
      return false;
    } else {
      return str.matches("\\d*");
    }
  }

  /**
   * 计算采用utf-8编码方式时字符串所占字节数
   */
  public static int getByteSize(String content) {
    int size = 0;
    if (null != content) {
      try {
        // 汉字采用utf-8编码时占3个字节
        size = content.getBytes("utf-8").length;
      } catch (UnsupportedEncodingException e) {
        logger.error("" + e);
      }
    }
    return size;
  }

  /**
   * 函数功能说明 ： 截取字符串拼接in查询参数
   */
  public static List<String> getInParam(String param) {
    boolean flag = param.contains(",");
    List<String> list = new ArrayList<String>();
    if (flag) {
      list = Arrays.asList(param.split(","));
    } else {
      list.add(param);
    }
    return list;
  }

  /**
   * 去掉null以及空格
   */
  public static String trim(Object str) {
    return str == null ? "" : str.toString().trim();
  }

  public static boolean hasText(String str) {
    return !"".equals(nonNull(str));
  }

  public static String nonNull(String str) {
    return str == null ? "" : str;
  }

  public static int getAsInt(String str) {
    return getAsInt(str, -1);
  }

  public static int getAsInt(String str, int defaultv) {
    if ((str == null) || ("".equals(str))) {
      return defaultv;
    }
    try {
      return Integer.parseInt(str, 10);
    } catch (Exception e) {
    }
    return defaultv;
  }

  public static long getAsLong(String str) {
    return getAsLong(str, -1L);
  }

  public static long getAsLong(String str, long defaultv) {
    if ((str == null) || ("".equals(str))) {
      return defaultv;
    }
    try {
      return Long.parseLong(str, 10);
    } catch (Exception e) {
    }
    return defaultv;
  }

  /**
   * 获取客户端的IP地址
   */
  public static String getIpAddr(HttpServletRequest request) {
    String ipAddress = null;
    ipAddress = request.getHeader("x-forwarded-for");
    if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getHeader("WL-Proxy-Client-IP");
    }
    if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
      ipAddress = request.getRemoteAddr();
      if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
        // 根据网卡取本机配置的IP
        InetAddress inet = null;
        try {
          inet = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
          logger.error("未知主机", e);
        }
        ipAddress = inet.getHostAddress();
      }

    }

    // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
    if (ipAddress != null && ipAddress.length() > 15) {
      if (ipAddress.indexOf(",") > 0) {
        ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
      }
    }
    return ipAddress;
  }

}
