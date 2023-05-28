package com.miku.bubble.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.miku.bubble.model.entity.Team;
import com.miku.bubble.model.entity.User;
import com.miku.bubble.model.request.TeamQuery;
import com.miku.bubble.model.vo.TeamUserVO;

import java.util.List;

/**
* @author gmj23
* @description 针对表【team(队伍)】的数据库操作Service
* @createDate 2023-04-09 20:35:47
*/
public interface TeamService extends IService<Team> {

    void validTeam(Team team, boolean add);

    long addTeam(Team team, User loginUser);

    /**
     * 搜索队伍
     * @param teamQuery
     * @return
     */
    List<TeamUserVO> listTeams(TeamQuery teamQuery);
}
