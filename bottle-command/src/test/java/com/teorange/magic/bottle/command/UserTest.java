//package com.teorange.magic.bottle.command;
//
//import cn.hutool.core.date.DateUtil;
//import com.baomidou.mybatisplus.mapper.EntityWrapper;
//import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
//import com.teorange.magic.bottle.api.mapper.TipOffLogMapper;
//import com.teorange.magic.bottle.api.service.MagicUserService;
//import java.util.Date;
//import lombok.extern.slf4j.Slf4j;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//
///**
// * Created by kellen on 2018/6/23.
// */
//@RunWith(SpringJUnit4ClassRunner.class)
//@SpringBootTest(classes = Application.class)
//@ActiveProfiles("dev")
//@Slf4j
//public class UserTest {
//
//
//  @Autowired
//  private MagicUserService magicUserService;
//
//  @Autowired
//  private TipOffLogMapper tipOffLogMapper;
//
//  @Test
//  public void updateTest() {
//    Date nowDateStart = DateUtil.beginOfDay(new Date());
//    Date startTime = DateUtil.offsetDay(nowDateStart, -1);
//    Date endTime = DateUtil.endOfDay(new Date());
//    tipOffLogMapper.insert(
//        new TipOffLogEntity().setToUserId(1012784423909650434L).setIp("127.0.0.1").setItemType(1)
//            .setItemId(1015895754804224002L).setUserId(1012939887452581890L)
//            .setCreateTime(new Date()));
//    Integer dayCount = tipOffLogMapper.selectCount(
//        new EntityWrapper<TipOffLogEntity>().eq("to_user_id", 1012784423909650434L)
//            .le("create_time", endTime)
//            .ge("create_time", startTime));
//    log.info("查询到x天内被举报的次数:{}", dayCount);
//  }
//
//}
