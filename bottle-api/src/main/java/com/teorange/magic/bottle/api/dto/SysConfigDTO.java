package com.teorange.magic.bottle.api.dto;

import cn.hutool.json.JSONUtil;
import java.io.Serializable;
import lombok.Data;

/**
 * Created by kellen on 2018/3/27.
 */
@Data
public class SysConfigDTO implements Serializable {

  private String key;
  private String value;
  private String remark;

  public static void main(String[] args) {
    SysConfigDTO sysConfigDTO=new SysConfigDTO();
    sysConfigDTO.setKey("CLOUD_STORAGE_CONFIG_KEY");
    sysConfigDTO.setValue("{\"type\":1,\"qiniuDomain\":\"http://img.shaiquan.net\",\"qiniuAccessKey\":\"xjT02kzY1DW0xCux5Bcyc5LWomgNE1evfa8_UwN7\",\"qiniuSecretKey\":\"-3azGALN-VK6km0brv59qr7P2dDVV7GdEV9yNAOs\",\"qiniuBucketName\":\"magicbottle\"}");
    System.out.println(JSONUtil.toJsonStr(sysConfigDTO));
  }

}
