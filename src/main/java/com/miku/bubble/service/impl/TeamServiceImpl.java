package com.miku.bubble.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.miku.bubble.common.ErrorCode;
import com.miku.bubble.exception.BusinessException;
import com.miku.bubble.mapper.TeamMapper;
import com.miku.bubble.model.entity.Team;
import com.miku.bubble.model.entity.User;
import com.miku.bubble.model.entity.UserTeam;
import com.miku.bubble.model.enums.TeamStatusEnum;
import com.miku.bubble.model.request.TeamQuery;
import com.miku.bubble.model.request.TeamUpdateRequest;
import com.miku.bubble.model.vo.TeamUserVO;
import com.miku.bubble.model.vo.UserVO;
import com.miku.bubble.service.TeamService;
import com.miku.bubble.service.UserService;
import com.miku.bubble.service.UserTeamService;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * @author gmj23
 * @description 针对表【team(队伍)】的数据库操作Service实现
 * @createDate 2023-04-09 20:35:47
 */
@Service
public class TeamServiceImpl extends ServiceImpl<TeamMapper, Team> implements TeamService {

    @Resource
    private UserTeamService userTeamService;
    @Resource
    private UserService userService;

    @Override
    public void validTeam(Team team, boolean add) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // TODO: 校验参数是否合法
        // 创建时，所有参数必须非空
//        if (add) {
//            if (StringUtils.isAnyBlank(content, job, place, education, loveExp) || ObjectUtils.anyNull(age, gender)) {
//                throw new BusinessException(ErrorCode.PARAMS_ERROR);
//            }
//        }
//        if (StringUtils.isNotBlank(content) && content.length() > 8192) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "内容过长");
//        }
//        if (reviewStatus != null && !TeamReviewStatusEnum.getValues().contains(reviewStatus)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR);
//        }
//        if (age != null && (age < 18 || age > 100)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "年龄不符合要求");
//        }
//        if (gender != null && !TeamGenderEnum.getValues().contains(gender)) {
//            throw new BusinessException(ErrorCode.PARAMS_ERROR, "性别不符合要求");
//        }
    }

    /**
     * 添加队伍
     *
     * @param team
     * @param loginUser
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public long addTeam(Team team, User loginUser) {
        if (team == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        final Long userId = team.getUserId();

        // 队伍人数数量限制
        int maxNum = Optional.ofNullable(team.getMaxNum()).orElse(0);
        if (maxNum <= 0 || maxNum > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍人数不满足要求！");
        }
        // 队伍名称长度限制
        String teamName = team.getName();
        if (StringUtils.isBlank(teamName) || teamName.length() > 20) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "? 你在干甚么?");
        }
        // 队伍描述长度限制
        String description = team.getDescription();
        if (StringUtils.isNotBlank(description) && description.length() > 512) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述太特么长了卧槽");
        }
        // 队伍状态判断
        Integer status = Optional.ofNullable(team.getStatus()).orElse(0);
        TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
        String password = team.getPassword();
        if (TeamStatusEnum.SECRET.equals(statusEnum) && (StringUtils.isBlank(password) || password.length() > 32)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "密码设置不正确！");
        }
        // 判断队伍过期时间
        Date expireTime = team.getExpireTime();
        if (new Date().after(expireTime)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "队伍时间设置不正确！");
        }

        // 创建队伍数量校验
        // TODO: 可能在一瞬间创建多个队伍
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Team::getUserId, userId);
        long hasTeamNum = this.count(queryWrapper);
        if (hasTeamNum > 5) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "最多创建5个队伍！");
        }
        // 保存队伍信息到队伍表
        team.setId(null);
        team.setUserId(userId);
        boolean saveT = this.save(team);
        Long teamId = team.getId();
        if (!saveT || teamId == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败!");
        }
        // 插入用户 => 队伍关系到关系表 (开启事务保证两表都插入成功)
        UserTeam userTeam = new UserTeam();
        userTeam.setUserId(userId);
        userTeam.setTeamId(teamId);
        userTeam.setJoinTime(new Date());
        boolean saveUt = userTeamService.save(userTeam);
        if (!saveUt) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "创建队伍失败!");
        }

        return teamId;
    }

    @Override
    public List<TeamUserVO> listTeams(TeamQuery teamQuery, boolean isAdmin) {
        LambdaQueryWrapper<Team> queryWrapper = new LambdaQueryWrapper<>();
        if (teamQuery != null) {
            Long id = teamQuery.getId();
            if (id != null && id > 0) {
                queryWrapper.eq(Team::getId, id);
            }
            String searchText = teamQuery.getSearchText();
            if (StringUtils.isNotBlank(searchText)) {
                queryWrapper.and(qw -> qw.like(Team::getSearchText, searchText).or().like(Team::getDescription, searchText));
            }
            String name = teamQuery.getName();
            if (StringUtils.isNotBlank(name)) {
                queryWrapper.eq(Team::getName, name);
            }
            String description = teamQuery.getDescription();
            if (StringUtils.isNotBlank(description)) {
                queryWrapper.like(Team::getDescription, description);
            }
            // 最大人数相等的
            Integer maxNum = teamQuery.getMaxNum();
            if (maxNum != null && maxNum > 0) {
                queryWrapper.eq(Team::getMaxNum, maxNum);
            }
            // 根据创建人来查询
            Long userId = teamQuery.getUserId();
            if (userId != null && userId > 0) {
                queryWrapper.eq(Team::getUserId, userId);
            }
            // 根据状态来查询
            Integer status = teamQuery.getStatus();
            TeamStatusEnum statusEnum = TeamStatusEnum.getEnumByValue(status);
            if (statusEnum == null) {
                statusEnum = TeamStatusEnum.PUBLIC;
            }
            if (!isAdmin && statusEnum.equals(TeamStatusEnum.PUBLIC)) {
                throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
            }
            queryWrapper.eq(Team::getStatus, statusEnum.getValue());

        }
        // 不展示已经过期的队伍
        queryWrapper.and(qw -> qw.gt(Team::getExpireTime, new Date()).or().isNull(Team::getExpireTime));

        List<TeamUserVO> teamUserVOList = new ArrayList<>();
        List<Team> teamList = this.list(queryWrapper);
        if (CollectionUtils.isEmpty(teamList)) {
            return new ArrayList<>();
        }
        // 关联查询用户信息
        for (Team team : teamList) {
            Long userId = team.getUserId();
            if (userId == null || userId <= 0) {
                continue;
            }
            User user = userService.getById(userId);
            TeamUserVO teamUserVO = new TeamUserVO();
            BeanUtil.copyProperties(team, teamUserVO);
            if (user != null) {
                UserVO userVO = new UserVO();
                BeanUtil.copyProperties(user, userVO);
                teamUserVO.setCreateUser(userVO);
            }
            teamUserVOList.add(teamUserVO);
        }
        // 查询队伍和创建人信息
        // select * from team t left join on user uon t.userId = u.id
        // 查询队伍和已加入队伍的队员信息
        // select * from team t join user_team ut on t.id = user_team.teamId
        return teamUserVOList;
    }

    @Override
    public boolean updateTeam(TeamUpdateRequest teamUpdateRequest, HttpServletRequest request) {
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, team);
        // 参数校验
        this.validTeam(team, false);
        User user = userService.getLoginUser(request);
        long id = teamUpdateRequest.getId();
        // 判断是否存在
        Team oldTeam = this.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 如果传入了密码 则直接把队伍改成加密状态
        if (StringUtils.isNotBlank(teamUpdateRequest.getPassword())) {
            teamUpdateRequest.setStatus(TeamStatusEnum.SECRET.getValue());
        }
        // 仅本人或管理员可修改
        if (!oldTeam.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        return this.updateById(team);
    }
}
