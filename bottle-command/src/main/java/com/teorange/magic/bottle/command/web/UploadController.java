package com.teorange.magic.bottle.command.web;

import static com.teorange.magic.bottle.api.constant.GlobalConstant.QINIU_TOKEN_KEY;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.core.exception.RRException;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.redis.utils.RedisCacheUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Maps;
import com.teorange.magic.bottle.api.dto.SysConfigDTO;
import com.teorange.magic.bottle.api.model.cache.QiniuToken;
import com.teorange.magic.bottle.api.plugins.oss.cloud.OSSFactory;
import com.teorange.magic.bottle.command.annotation.Login;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by kellen on 2018/5/27.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/oss")
public class UploadController {


  private OSSFactory ossFactory;

  private RedisCacheUtil redisCacheUtil;

  /**
   * 上传文件
   */
  @PostMapping("/upload")
  public R upload(@RequestParam("file") MultipartFile file) throws Exception {
    Map<String, Object> data = Maps.newHashMap();
    if (file.isEmpty()) {
      throw new RRException("上传文件不能为空");
    }
    //上传文件
    String suffix = file.getOriginalFilename()
        .substring(file.getOriginalFilename().lastIndexOf("."));
    String url = ossFactory.build().uploadSuffix(file.getBytes(), suffix);
//    if (!url.isEmpty()) {
//      File imgfile = new File(url);
//      BufferedImage sourceImg = ImageIO.read(new FileInputStream(imgfile));
//      data.put("width", sourceImg.getWidth());
//      data.put("height", sourceImg.getHeight());
//    }
    data.put("url", url);
    return R.ok().put("data", data);
  }


  /**
   * 获取七牛云token
   */
  @PostMapping("/getQiniuToken")
  @Login
  public R getQiniuToken() {
    long expireTime = 3600 * 24L;
    //从缓存中获取
    QiniuToken qiniuToken = redisCacheUtil.get(QINIU_TOKEN_KEY, QiniuToken.class);
    if (null != qiniuToken) {
      return R.ok().put("data", qiniuToken);
    }
    //已过期重新获取
    String token = ossFactory.build().getToken(expireTime + 60);
    if (StrUtil.isEmpty(token)) {
      return R.error("获取失败");
    }
    QiniuToken cacheToken = new QiniuToken();
    cacheToken.setToken(token);
    cacheToken.setExpireTime(DateUtil.offsetHour(new Date(), 24));
    //过期时间24小时
    redisCacheUtil
        .set(QINIU_TOKEN_KEY, cacheToken, expireTime, TimeUnit.SECONDS);
    return R.ok().put("data", cacheToken);
  }

  public static void main(String[] args) {
    String json = "{\"type\":1,\"qiniuDomain\":\"http://img.shaiquan.net\",\"qiniuAccessKey\":\"xjT02kzY1DW0xCux5Bcyc5LWomgNE1evfa8_UwN7\",\"qiniuSecretKey\":\"-3azGALN-VK6km0brv59qr7P2dDVV7GdEV9yNAOs\",\"qiniuBucketName\":\"magicbottle\"}";
    SysConfigDTO sysConfigDTO = new SysConfigDTO();
    sysConfigDTO.setKey("CLOUD_STORAGE_CONFIG_KEY");
    sysConfigDTO.setValue(
        "{\"type\":1,\"qiniuDomain\":\"http://img.shaiquan.net\",\"qiniuAccessKey\":\"xjT02kzY1DW0xCux5Bcyc5LWomgNE1evfa8_UwN7\",\"qiniuSecretKey\":\"-3azGALN-VK6km0brv59qr7P2dDVV7GdEV9yNAOs\",\"qiniuBucketName\":\"magicbottle\"}");
    sysConfigDTO.setRemark("配置信息");
    System.out.println(JSON.toJSON(sysConfigDTO));
    String config = "{\"remark\":\"配置信息\",\"value\":\"{\\\"type\\\":1,\\\"qiniuDomain\\\":\\\"http://img.shaiquan.net\\\",\\\"qiniuAccessKey\\\":\\\"xjT02kzY1DW0xCux5Bcyc5LWomgNE1evfa8_UwN7\\\",\\\"qiniuSecretKey\\\":\\\"-3azGALN-VK6km0brv59qr7P2dDVV7GdEV9yNAOs\\\",\\\"qiniuBucketName\\\":\\\"magicbottle\\\"}\",\"key\":\"CLOUD_STORAGE_CONFIG_KEY\"}";
  }

}
