package com.teorange.magic.bottle.api.plugins.wordfilter;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.teorange.magic.bottle.api.domain.FilterWordEntity;
import com.teorange.magic.bottle.api.service.FilterWordService;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class WordFilter {

  @Autowired
  private FilterWordService filterWordService;

  private enum FilterState {SUCCESS, ERROR}


  private enum KeywordType {FILTER, STOP}


  private FilterState FilterStatus = FilterState.ERROR;
  private Map<Integer, Integer> set = new HashMap<Integer, Integer>(); // 存储首字
  private Map<Integer, WordNode> nodes = new HashMap<>(); // 存储节点
  private Set<Integer> stopwdSet = new HashSet<Integer>(); // 停顿词
  private char SIGN = '*'; // 敏感词过滤替换


  /**
   * 初始化
   */
  @PostConstruct
  public void init() {
    // 获取敏感词
    if (FilterStatus == FilterState.ERROR) {
      long time1 = System.currentTimeMillis();
      addSensitiveWord(readWordFromFile(KeywordType.FILTER));
      addStopWord(StopWordUtils.getStopWord());
      long time2 = System.currentTimeMillis();
      log.info("敏感词数量:" + nodes.size());
      log.info("加载过滤库时间" + (time2 - time1) + "ms");
      FilterStatus = FilterState.SUCCESS;
    }
  }

  /**
   * 设置
   */
  public void resetInit() {
    FilterStatus = FilterState.ERROR;
    set.clear();
    nodes.clear();
    init();
    log.info("重新加载敏感词，敏感词数量:" + nodes.size());
  }

  /**
   * 增加敏感词
   */
  private List<String> readWordFromFile(KeywordType type) {
    List<String> words = new ArrayList<String>();
    List<FilterWordEntity> data = null;
    data = filterWordService.selectList(new EntityWrapper<>());
    for (FilterWordEntity entity : data) {
      words.add(entity.getContent());
    }
    data.clear();
    return words;
  }

  /**
   * 增加停顿词
   */
  private void addStopWord(final List<String> words) {
    if (words != null && words.size() > 0) {
      char[] chs;
      for (String curr : words) {
        chs = curr.toCharArray();
        for (char c : chs) {
          stopwdSet.add(charConvert(c));
        }
      }
    }
  }

  /**
   * 添加DFA节点
   */
  private void addSensitiveWord(final List<String> words) {
    if (words != null && words.size() > 0) {
      char[] chs;
      int fchar;
      int lastIndex;
      WordNode fnode; // 首字母节点
      for (String curr : words) {
        chs = curr.toCharArray();
        if (chs.length == 0) {
          continue;
        }
        fchar = charConvert(chs[0]);
        if (!set.containsKey(fchar)) {// 没有首字定义
          set.put(fchar, fchar);// 首字标志位 可重复add,反正判断了，不重复了
          fnode = new WordNode(fchar, chs.length == 1);
          nodes.put(fchar, fnode);
        } else {
          fnode = (WordNode) nodes.get(fchar);
          if (fnode != null && !fnode.isLast() && chs.length == 1) {
            fnode.setLast(true);
          }
        }
        lastIndex = chs.length - 1;
        for (int i = 1; i < chs.length; i++) {
          fnode = fnode.addIfNoExist(charConvert(chs[i]), i == lastIndex);
        }
      }
    }
  }

  /**
   * 过滤判断 将敏感词转化为成屏蔽词
   */
  public String doFilter(final String src) {
    init();
    char[] chs = src.toCharArray();
    int length = chs.length;
    int currc;
    int k;
    WordNode node;
    for (int i = 0; i < length; i++) {
      currc = charConvert(chs[i]);
      if (!set.containsKey(currc)) {
        continue;
      }
      node = nodes.get(currc);// 日 2
      if (node == null)// 其实不会发生，习惯性写上了
      {
        continue;
      }
      boolean couldMark = false;
      int markNum = -1;
      if (node.isLast()) {// 单字匹配（日）
        couldMark = true;
        markNum = 0;
      }
      k = i;
      for (; ++k < length; ) {
        int temp = charConvert(chs[k]);
        if (stopwdSet.contains(temp)) {
          continue;
        }
        node = node.querySub(temp);
        if (node == null)// 没有了
        {
          break;
        }
        if (node.isLast()) {
          couldMark = true;
          markNum = k - i;// 3-2
        }
      }
      if (couldMark) {
        for (k = 0; k <= markNum; k++) {
          chs[k + i] = SIGN;
        }
        i = i + markNum;
      }
    }

    return new String(chs);
  }

  /**
   * 是否包含敏感词
   */
  public boolean isContains(final String src) {
    //this.nodes = redisUtils.get("word_node", Map.class);
    //this.set = redisUtils.get("word_set", Map.class);
    log.info("是否包含敏感词方法，敏感词数量:" + nodes.size());
    char[] chs = src.toCharArray();
    int length = chs.length;
    int currc;
    int k;
    WordNode node;
    for (int i = 0; i < length; i++) {
      currc = charConvert(chs[i]);
      if (!set.containsKey(currc)) {
        continue;
      }
      node = nodes.get(currc);// 日 2
      if (node == null)// 其实不会发生，习惯性写上了
      {
        continue;
      }
      boolean couldMark = false;
      if (node.isLast()) {// 单字匹配（日）
        couldMark = true;
      }
      // 继续匹配（日你/日你妹），以长的优先
      // 你-3 妹-4 夫-5
      k = i;
      for (; ++k < length; ) {
        int temp = charConvert(chs[k]);
        if (stopwdSet.contains(temp)) {
          continue;
        }
        node = node.querySub(temp);
        if (node == null)// 没有了
        {
          break;
        }
        if (node.isLast()) {
          couldMark = true;
        }
      }
      if (couldMark) {
        return true;
      }
    }

    return false;
  }

  /**
   * 大写转化为小写 全角转化为半角
   */
  private int charConvert(char src) {
    int r = BCConvert.qj2bj(src);
    return (r >= 'A' && r <= 'Z') ? r + 32 : r;
  }

}