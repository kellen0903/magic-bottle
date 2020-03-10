# magic-bottle

## 项目介绍
`magic-bottle`是一套匿名社交系统，采用DDD+CQRS+Event Sourcing架构，采用了Spring Boot、Mybatis Plus、Axon Framework等核心技术，同时提供ios、andriod、web后台的开源项目。

## 项目特色
- 使用极光推送实现IM聊天
- 完全开源，并提供ios、andriod客户端源码，可直接商用；
- DDD真正落地的开源项目；
- 提供完整的匿名社交解决方案；
- 部署方便，支持分布式

## 系统功能
从业务功能上，目前主要有以下功能：
- 帖子发布、评论、点赞
- 帖子敏感词过滤
- 针对帖子进行匿名聊天
- 举报功能
- 用户注册、登陆


## 组织结构

``` lua
magic-bottle
├── bottle-admin -- 管理服务端模块
├── bottle-api -- api接口模块
├── bottle-commond -- app模块
└── web-admin -- 管理前端页面
```

## 快速开始
1.配置环境：
- Mysql
- JDK1.8或以上
- Maven
- Nodejs
- Mongodb
- Redis

2.数据库导入doc/sql下的脚本：xmpdb.sql

3.修改bottle-admin、bottle-commond下application.yml配置文件中redis、mongodb、mysql的为自己环境的配置

4.将lib下的jar包导入idea

5.分别启动bottle-admin和bottle-commond项目


6.启动管理后台前端
```
npm install -g cnpm --registry=https://registry.npm.taobao.org
cd magic-bottle/web-admin
cnpm install
cnpm run dev
```
7.打开浏览器localhost:8001访问后台管理系统

# 问题反馈

如有问题欢迎提交issue，获取ios、andriod端源码请加qq群：1079276490

