package com.teorange.magic.bottle.api.plugins.oss.cloud.vo;

import lombok.Data;

/**
 * 自定义七牛云回复内容
 * created by cjj
 */
@Data
public class QiniuMyPutRet {

    public String key;
    public String hash;
    public String bucket;
    public long fsize;
}
