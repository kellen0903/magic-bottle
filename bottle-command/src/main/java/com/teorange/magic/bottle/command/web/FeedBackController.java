package com.teorange.magic.bottle.command.web;

import cn.teorange.framework.core.utils.R;
import cn.teorange.framework.core.validator.ValidatorUtils;
import cn.teorange.framework.core.validator.group.AddGroup;
import com.teorange.magic.bottle.api.domain.FeedBackEntity;
import com.teorange.magic.bottle.api.domain.MagicUserEntity;
import com.teorange.magic.bottle.api.dto.FeedBackDTO;
import com.teorange.magic.bottle.api.service.FeedBackService;
import com.teorange.magic.bottle.command.annotation.Login;
import com.teorange.magic.bottle.command.annotation.LoginUser;
import java.util.Date;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by kellen on 2018/6/10.
 */
@RestController
@RequestMapping("/feedBack")
@AllArgsConstructor
public class FeedBackController {

  private FeedBackService feedBackService;


  /**
   * 提交反馈意见
   */
  @PostMapping("/add")
  @Login
  public R add(@LoginUser MagicUserEntity magicUserEntity, @RequestBody FeedBackDTO feedBackDTO) {
    ValidatorUtils.validateEntity(feedBackDTO, AddGroup.class);
    FeedBackEntity feedBackEntity = new FeedBackEntity();
    BeanUtils.copyProperties(feedBackDTO, feedBackEntity);
    feedBackEntity.setCreateBy(magicUserEntity.getId().toString());
    feedBackEntity.setCreateTime(new Date());
    feedBackService.insert(feedBackEntity);
    return R.ok();
  }

}
