package com.teorange.magic.bottle.admin.modules.count;

import cn.teorange.framework.core.utils.R;
import com.teorange.magic.bottle.api.domain.ChatEntity;
import com.teorange.magic.bottle.api.service.MagicUserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 用户维度分组统计
 * created by cjj
 */
@RestController
@RequestMapping("count/user")
@AllArgsConstructor
@Slf4j
public class UserGoupByCountController {

    private MagicUserService userService;

    @RequestMapping("/list")
    public R list(@RequestParam("countType") Integer countType){
        List<Map> resultMap = userService.goupByCount(countType);

        return R.ok().put("data", resultMap);
    }
}
