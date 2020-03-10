package com.teorange.magic.bottle.api.service;

import cn.hutool.core.map.MapUtil;
import cn.teorange.framework.mybatisplus.utils.PageUtils;
import cn.teorange.framework.mybatisplus.utils.Query;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.mapper.Wrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.teorange.magic.bottle.api.domain.MessageEntity;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.mapper.MessageMapper;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;


@Service("messageService")
public class MessageService extends ServiceImpl<MessageMapper, MessageEntity> {

  public PageUtils queryPage(Map<String, Object> params) {
    Page<MessageEntity> page = this.selectPage(
        new Query<MessageEntity>(params).getPage(),
        new EntityWrapper<>()
    );

    return new PageUtils(page);
  }

  public PageUtils queryPageList(Map<String, Object> params) {

    Long chatId = MapUtil.getLong(params, "chatId");
    //分页查询条件
    Page<MessageEntity> page = new Query<MessageEntity>(params).getPage();
    Wrapper<MessageEntity> wrapper = new EntityWrapper<MessageEntity>().where("chat_id={0}", chatId)
            .orderBy("create_time",false);

    //分装分页
    List<MessageEntity> messageEntities = baseMapper.queryPageList(page, wrapper);
    page.setRecords(messageEntities);

    return new PageUtils(page);
  }
}
