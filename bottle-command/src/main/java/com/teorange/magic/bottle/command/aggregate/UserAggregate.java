package com.teorange.magic.bottle.command.aggregate;

import static org.axonframework.commandhandling.model.AggregateLifecycle.apply;

import cn.hutool.core.lang.Validator;
import cn.hutool.core.util.StrUtil;
import com.teorange.magic.bottle.api.command.CompleteUserInfoCommand;
import com.teorange.magic.bottle.api.command.CreateUserCommand;
import com.teorange.magic.bottle.api.command.LoginCommand;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.event.LoginedEvent;
import com.teorange.magic.bottle.api.event.UserCreatedEvent;
import com.teorange.magic.bottle.api.event.UserInfoCompletedEvent;
import java.util.Date;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.commandhandling.model.AggregateIdentifier;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;

/**
 * Created by kellen on 2018/5/20.
 */
@NoArgsConstructor
@Aggregate(repository = "userAggregateRepository")
@ToString
@Slf4j
@Getter
@Setter
public class UserAggregate {


  @AggregateIdentifier
  private String openId;


  private Long id;

  /**
   * 用户昵称
   */
  private String nickName;
  /**
   * 最后一次登录ip地址
   */
  private String lastLoginIp;
  /**
   * 性别 1：男 2：女 3：未知
   */
  private Integer sex;
  /**
   * idfa
   */
  private String idfa;
  /**
   * ua
   */
  private String ua;
  /**
   * 手机名称
   */
  private String mobileName;
  /**
   * 手机型号
   */
  private String mobileVersion;
  /**
   * imei
   */
  private String imei;
  /**
   * 来源 1：ios  2:andriod
   */
  private Integer sourceType;
  /**
   * 年龄描述
   */
  private String age;
  /**
   * 最后登录时间
   */
  private Date lastLoginTime;
  /**
   * 密码
   */
  private String password;
  /**
   * 登录方式 1：微信 2:QQ
   */
  private Integer loginType;
  /**
   * 极光推送id
   */
  private String jpushId;
  /**
   * 创建时间
   */
  private Date createTime;
  /**
   * 1：正常 2：禁言
   */
  private Integer status;


  public UserAggregate(CreateUserCommand command) {
    apply(new UserCreatedEvent().setUserDTO(command.getUserDTO()));
  }


  @EventHandler
  public void on(UserCreatedEvent event) {
    UserDTO userDTO = event.getUserDTO();
    BeanUtils.copyProperties(userDTO, this);
    this.createTime = new Date();
    this.status = 1;
  }


  @CommandHandler
  public String handle(LoginCommand command) {
    LoginedEvent event = new LoginedEvent();
    BeanUtils.copyProperties(command, event);
    event.setId(this.getId());
    event.setOldJpushId(this.jpushId);
    apply(event);
    return this.jpushId;
  }


  @EventHandler
  public void on(LoginedEvent event) {
    BeanUtils.copyProperties(event, this);
  }

  /**
   * 完善用户资料
   */
  @CommandHandler
  public void hanlde(CompleteUserInfoCommand command) {
    apply(new UserInfoCompletedEvent(command.getOpenId(), command.getSex(), command.getNickName(),
        command.getAge(), this.id));
  }


  @EventHandler
  public void on(UserInfoCompletedEvent event) {
    if (StrUtil.isNotEmpty(event.getAge())) {
      this.age = event.getAge();
    }
    if (StrUtil.isNotEmpty(event.getNickName())) {
      this.nickName = event.getNickName();
    }

    if (Validator.isNotEmpty((event.getSex()))) {
      this.sex = event.getSex();
    }

  }
}
