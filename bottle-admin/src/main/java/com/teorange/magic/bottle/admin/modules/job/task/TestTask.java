package com.teorange.magic.bottle.admin.modules.job.task;


import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.teorange.framework.axon.util.IdWorker;
import cn.teorange.framework.redis.utils.RedisCacheUtil;
import com.teorange.magic.bottle.admin.modules.sys.entity.SysUserEntity;
import com.teorange.magic.bottle.admin.modules.sys.service.SysUserService;
import com.teorange.magic.bottle.api.command.AddPostCommand;
import com.teorange.magic.bottle.api.dto.PostDTO;
import com.teorange.magic.bottle.api.dto.UserDTO;
import com.teorange.magic.bottle.api.service.PostService;
import com.teorange.magic.bottle.api.service.SysConfigService;
import java.io.File;
import java.io.InputStream;
import java.util.Date;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 测试定时任务(演示Demo，可删除)
 *
 * testTask为spring bean的名称
 *
 * @author Mark sunlightcs@gmail.com
 * @since 1.2.0 2016-11-28
 */
@Component("testTask")
public class TestTask {

  private Logger logger = LoggerFactory.getLogger(getClass());

  @Autowired
  private SysUserService sysUserService;

  @Autowired
  private PostService postService;

  @Autowired
  private RedisCacheUtil redisCacheUtil;

  @Autowired
  private CommandGateway commandGateway;

  @Autowired
  private SysConfigService sysConfigService;

  @Value("${file.path}")
  private String filePath;

  // 记录当前行数
  private static final String SHEET_LINENUMBER = "sheet_lineNumber";

  public void test(String params) {
    logger.info("我是带参数的test方法，正在被执行，参数为：" + params);

    try {
      Thread.sleep(1000L);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    SysUserEntity user = sysUserService.selectById(1L);
    System.out.println(ToStringBuilder.reflectionToString(user));

  }


  public void test2() {
    logger.info("我是不带参数的test2方法，正在被执行");
  }


  public void excelImport(String params) throws Exception {
    logger.info("Excel表单导入方法，正在被执行，参数为{}", params);
    int param = Integer.parseInt(params);
    // List<PostEntity> list = new ArrayList<PostEntity>();
    InputStream inputStream = FileUtils.openInputStream(new File(filePath));
    HSSFWorkbook book = new HSSFWorkbook(inputStream);
//    HSSFWorkbook book = new HSSFWorkbook(
//        new FileInputStream(ResourceUtils.getFile("classpath:post-import.xls")));

    HSSFSheet sheet = book.getSheetAt(0);

    //总行数
    int totalRowNum = sheet.getLastRowNum();

    //当前读取的行数
    int currentLine = 2;
    String currentLineConfig = sysConfigService.getValue(SHEET_LINENUMBER);
    if (StrUtil.isNotEmpty(currentLineConfig)) {
      currentLine = Integer.valueOf(currentLineConfig);
    }
    //本次要读取的行数
    int readNumber = currentLine + param;
    if (totalRowNum - readNumber < param) {
      readNumber = totalRowNum;
    }
    sysConfigService.updateValueByKey(SHEET_LINENUMBER, String.valueOf(readNumber));
    // 跳出执行
    if (currentLine > totalRowNum) {
      return;
    }
    Integer num = 0;
    for (int i = currentLine; i < readNumber; i++) {
      HSSFRow row = sheet.getRow(i);
      Long userId = 0L;
      Integer sex = 0;
      Long tagId = 0L;
      if (row.getCell(0) != null) {
        row.getCell(0).setCellType(Cell.CELL_TYPE_STRING);
        userId = Long.parseLong(row.getCell(0).getStringCellValue());
      }
      String nickName = row.getCell(1).getStringCellValue();
      if (row.getCell(2) != null) {
        row.getCell(2).setCellType(Cell.CELL_TYPE_STRING);
        sex = Integer.parseInt(row.getCell(2).getStringCellValue());
      }
      String content = row.getCell(3).getStringCellValue();
      if (row.getCell(4) != null) {
        row.getCell(4).setCellType(Cell.CELL_TYPE_STRING);
        tagId = Long.parseLong(row.getCell(4).getStringCellValue());
      }
      String tagName = row.getCell(5).getStringCellValue();
      String temperature = row.getCell(6).getStringCellValue();
      String weather = row.getCell(7).getStringCellValue();
      AddPostCommand command = new AddPostCommand();
      PostDTO postDTO = new PostDTO();
      postDTO
          .setPostId(IdWorker.getId())
          .setPostUser(new UserDTO().setId(userId).setSex(sex).setNickName(nickName))
          .setNickName(nickName)
          .setContent(content)
          .setTemperature(temperature)
          .setWeather(weather)
          .setTagId(tagId)
          .setTagName(tagName)
          .setCreateTime(new Date())
          .setCommentCount(0L)
          .setLikeCount(0L)
          .setPublishStatus(1)
          .setWeek(DateUtil.thisDayOfWeekEnum().toChinese())
          .setCommentFlag(0);
      command.setPostDTO(postDTO);
      logger.info("发送新增帖子命令参数:{}", command);
      commandGateway.send(command);
      num++;
    }
    logger.info("Excel表单导入方法，本次新增数据数量{}", num);

    //postService.insertBatch(list);

  }
}
