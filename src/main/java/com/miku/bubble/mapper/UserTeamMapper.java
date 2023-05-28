package com.miku.bubble.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miku.bubble.model.entity.UserTeam;
import org.apache.ibatis.annotations.Mapper;

/**
* @author gmj23
* @description 针对表【user_team(用户队伍关系)】的数据库操作Mapper
* @createDate 2023-04-09 20:35:53
* @Entity generator.entity.UserTeam
*/
@Mapper
public interface UserTeamMapper extends BaseMapper<UserTeam> {

}




