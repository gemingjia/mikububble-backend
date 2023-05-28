package com.miku.bubble.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miku.bubble.mapper.UserTeamMapper;
import com.miku.bubble.model.entity.UserTeam;
import com.miku.bubble.service.UserTeamService;
import org.springframework.stereotype.Service;

/**
* @author gmj23
* @description 针对表【user_team(用户队伍关系)】的数据库操作Service实现
* @createDate 2023-04-09 20:35:53
*/
@Service
public class UserTeamServiceImpl extends ServiceImpl<UserTeamMapper, UserTeam>
    implements UserTeamService{

}




