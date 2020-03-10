package com.teorange.magic.bottle.admin.modules.count;

import cn.teorange.framework.core.utils.R;
import com.teorange.magic.bottle.api.service.ChatService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 活跃会话
 * created by cjj
 */
@RestController
@RequestMapping("count/chat")
@AllArgsConstructor
@Slf4j
public class ChatGoupByCountController {

    private ChatService chatService;

    @RequestMapping("/list")
    public R list(@RequestParam("countType") Integer countType){
        List<Map> resultMap = chatService.goupByCount(countType);

        return R.ok().put("data", resultMap);
    }
}
