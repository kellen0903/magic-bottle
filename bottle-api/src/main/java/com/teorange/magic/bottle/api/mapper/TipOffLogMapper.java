package com.teorange.magic.bottle.api.mapper;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.teorange.magic.bottle.api.domain.TipOffLogEntity;
import com.teorange.magic.bottle.api.dto.TipOffReportDTO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.session.RowBounds;

@Mapper
public interface TipOffLogMapper extends BaseMapper<TipOffLogEntity> {

  public Integer queryTipOffCount(@Param("itemId") Long itemId);


  List<TipOffReportDTO> countReport(RowBounds rowBounds, @Param("ew") Map<String, Object> param);

}
