package com.miku.bubble.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miku.bubble.model.entity.Team;
import org.apache.ibatis.annotations.Mapper;

import javax.annotation.ManagedBean;

/**
* @author gmj23
* @description 针对表【team(队伍)】的数据库操作Mapper
* @createDate 2023-04-09 20:35:47
* @Entity generator.entity.Team
*/
@Mapper
public interface TeamMapper extends BaseMapper<Team> {

}




