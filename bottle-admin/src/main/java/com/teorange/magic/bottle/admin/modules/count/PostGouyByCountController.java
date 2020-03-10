package com.teorange.magic.bottle.admin.modules.count;

import cn.teorange.framework.core.utils.R;
import com.teorange.magic.bottle.api.service.PostService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 新增帖子
 * created by cjj
 */
@RestController
@RequestMapping("count/post")
@AllArgsConstructor
@Slf4j
public class PostGouyByCountController {

    private PostService postService;

    @RequestMapping("/list")
    public R list(@RequestParam("countType") Integer countType){
        List<Map> resultMap = postService.goupByCount(countType);

        return R.ok().put("data", resultMap);
    }
}
