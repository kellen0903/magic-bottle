package com.teorange.magic.bottle.command.web;

import cn.teorange.framework.core.utils.IPUtils;
import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.AddGroup;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.domain.PostEntity;
import com.teorange.magic.bottle.api.domain.PostReplyEntity;
import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
import com.teorange.magic.bottle.api.dto.TipOffDTO;
import com.teorange.magic.bottle.api.service.PostReplyService;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import com.teorange.magic.bottle.command.service.TipOffService;
import java.util.Date;
import java.util.concurrent.ThreadPoolExecutor;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/6/3.举报接口
 */
@RestController
@RequestMapping("/tipOff")
@AllArgsConstructor
public class TipOffController {


  private TipOffService tipOffService;


  private ThreadPoolExecutor messageThreadPool;

  private PostService postService;

  private PostReplyService postReplyService;

  /**
   * 添加举报
   */
  @PostMapping("/add")
  @Login
  public R add(@RequestBody TipOffDTO tipOffDTO, @LoginUser MagicUserEntity magicUserEntity,
      HttpServletRequest request) {
    ValidatorUtils.validateEntity(tipOffDTO, AddGroup.class);
    String ip = IPUtils.getIpAddr(request);
    TipOffLogEntity entity = new TipOffLogEntity();
    BeanUtils.copyProperties(tipOffDTO, entity);
    entity.setIp(ip);
    entity.setUserId(magicUserEntity.getId());
    entity.setCreateTime(new Date());
    Integer itemType = tipOffDTO.getItemType();
    Long toUserId = null;
    //举报类型  1：魔瓶 2：评论
    switch (itemType) {
      case 1:
        PostEntity postEntity = postService.selectById(tipOffDTO.getItemId());
        if (null == postEntity) {
          return R.error("魔瓶不存在");
        }
        //发布魔瓶的用户
        toUserId = postEntity.getUserId();
        entity.setToUserId(toUserId);
        break;
      //2：评论
      case 2:
        PostReplyEntity postReplyEntity = postReplyService.selectById(tipOffDTO.getItemId());
        if (null == postReplyEntity) {
          return R.error("评论不存在");
        }
        toUserId = postReplyEntity.getFromUid();
        entity.setToUserId(toUserId);
        break;
      default:
        break;
    }
    //先查询次用户是否有举报过
    if (null != toUserId) {
      Integer count = tipOffService.selectCount(
          new EntityWrapper<TipOffLogEntity>().eq("user_id", magicUserEntity.getId())
              .eq("item_id", tipOffDTO.getItemId()));
      //一个用户对同一帖子只能举报一次
      if (count > 0) {
        return R.error("你已经举报，请耐心等待管理员处理");
      }
      tipOffService.insert(entity);
      //被举报之后的处理
      tipOffService.postTipOff(entity);
    }
    return R.ok();
  }


}
