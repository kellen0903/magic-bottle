/**
 * Copyright 2018 人人开源 http://www.renren.io <p> Licensed under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a
 * copy of the License at <p> http://www.apache.org/licenses/LICENSE-2.0 <p> Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See
 * the License for the specific language governing permissions and limitations under the License.
 */

package com.teorange.magic.bottle.api.plugins.oss.cloud;

import cn.teorange.framework.core.exception.RRException;
import com.alibaba.fastjson.JSON;
import com.qiniu.common.Zone;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.UploadManager;
import com.qiniu.util.Auth;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import com.qiniu.util.StringMap;
import com.teorange.magic.bottle.api.plugins.oss.cloud.vo.QiniuMyPutRet;
import org.apache.commons.io.IOUtils;

/**
 * 七牛云存储
 *
 * @author chenshun
 * @email sunlightcs@gmail.com
 * @date 2017-03-25 15:41
 */
public class QiniuCloudStorageService extends CloudStorageService {

  private UploadManager uploadManager;
  private String token;

  public QiniuCloudStorageService(CloudStorageConfig config) {
    this.config = config;

    //初始化
    init();
  }

  private void init() {
    StringMap putPolicy = new StringMap();
    putPolicy.put("returnBody", "{\"key\":\"$(key)\",\"hash\":\"$(etag)\",\"bucket\":\"$(bucket)\",\"fsize\":$(fsize)}");
    uploadManager = new UploadManager(new Configuration(Zone.autoZone()));
    token = Auth.create(config.getQiniuAccessKey(), config.getQiniuSecretKey()).
            uploadToken(config.getQiniuBucketName(),null, 3600, putPolicy);
  }

  @Override
  public String upload(byte[] data, String path) {
    Map<String,Object> resultMap = new HashMap<>();
    try {
      Response res = uploadManager.put(data, path, token);
      QiniuMyPutRet myPutRet=res.jsonToObject(QiniuMyPutRet.class);

      if (!res.isOK()) {
        throw new RuntimeException("上传七牛出错：" + res.toString());
      }
      resultMap.put("url", config.getQiniuDomain() + "/" + path);
      resultMap.put("fsize", myPutRet.getFsize());
      resultMap.put("type", "qiniu");
    } catch (Exception e) {
      throw new RRException("上传文件失败，请核对七牛配置信息", e);
    }

    return JSON.toJSONString(resultMap);
  }

  @Override
  public String upload(InputStream inputStream, String path) {
    try {
      byte[] data = IOUtils.toByteArray(inputStream);
      return this.upload(data, path);
    } catch (IOException e) {
      throw new RRException("上传文件失败", e);
    }
  }

  @Override
  public String uploadSuffix(byte[] data, String suffix) {
    return upload(data, getPath(config.getQiniuPrefix(), suffix));
  }

  @Override
  public String uploadSuffix(InputStream inputStream, String suffix) {
    return upload(inputStream, getPath(config.getQiniuPrefix(), suffix));
  }

  @Override
  public String getToken(long expireTime) {
    String accessKey = config.getQiniuAccessKey();
    String secretKey = config.getQiniuSecretKey();
    String bucket = config.getQiniuBucketName();
    Auth auth = Auth.create(accessKey, secretKey);
    String upToken = auth.uploadToken(bucket, null, expireTime, null);
    return upToken;
  }
}
