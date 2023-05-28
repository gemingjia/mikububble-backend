package com.miku.bubble.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.miku.bubble.common.BaseResponse;
import com.miku.bubble.common.DeleteRequest;
import com.miku.bubble.common.ErrorCode;
import com.miku.bubble.common.ResultUtils;
import com.miku.bubble.constant.CommonConstant;
import com.miku.bubble.exception.BusinessException;
import com.miku.bubble.model.entity.Team;
import com.miku.bubble.model.entity.User;
import com.miku.bubble.model.request.TeamAddRequest;
import com.miku.bubble.model.request.TeamQuery;
import com.miku.bubble.model.request.TeamUpdateRequest;
import com.miku.bubble.model.vo.TeamUserVO;
import com.miku.bubble.service.TeamService;
import com.miku.bubble.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 帖子接口
 *
 * @author miku
 */
@RestController
@RequestMapping("/team")
@Slf4j
public class TeamController {

    @Resource
    private TeamService teamService;

    @Resource
    private UserService userService;

    // region 增删改查

    /**
     * 创建
     *
     * @param teamAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addTeam(@RequestBody TeamAddRequest teamAddRequest, HttpServletRequest request) {
        if (teamAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamAddRequest, team);
        // 校验
//        teamService.validTeam(team, true);
        User loginUser = userService.getLoginUser(request);
        team.setUserId(loginUser.getId());
        long teamId = teamService.addTeam(team, loginUser);
        return ResultUtils.success(teamId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteTeam(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Team oldTeam = teamService.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可删除
        if (!oldTeam.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = teamService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新
     *
     * @param teamUpdateRequest
     * @param request
     * @return
     */
    @PostMapping("/update")
    public BaseResponse<Boolean> updateTeam(@RequestBody TeamUpdateRequest teamUpdateRequest,
                                            HttpServletRequest request) {
        if (teamUpdateRequest == null || teamUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = new Team();
        BeanUtils.copyProperties(teamUpdateRequest, team);
        // 参数校验
        teamService.validTeam(team, false);
        User user = userService.getLoginUser(request);
        long id = teamUpdateRequest.getId();
        // 判断是否存在
        Team oldTeam = teamService.getById(id);
        if (oldTeam == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 仅本人或管理员可修改
        if (!oldTeam.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = teamService.updateById(team);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get")
    public BaseResponse<Team> getTeamById(long id) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team team = teamService.getById(id);
        return ResultUtils.success(team);
    }

    /**
     * 获取队伍列表
     *
     * @param teamQuery
     * @return
     */
    @GetMapping("/list")
    public BaseResponse<List<TeamUserVO>> listTeam(TeamQuery teamQuery) {
        if (teamQuery == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        List<TeamUserVO> teamList = teamService.listTeams(teamQuery);
        return ResultUtils.success(teamList);
    }

    /**
     * 分页获取列表
     *
     * @param teamQueryRequest
     * @param request
     * @return
     */
    @GetMapping("/list/page")
    public BaseResponse<Page<Team>> listTeamByPage(TeamQuery teamQueryRequest, HttpServletRequest request) {
        if (teamQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Team teamQuery = new Team();
        BeanUtils.copyProperties(teamQueryRequest, teamQuery);
        long current = teamQueryRequest.getCurrent();
        long size = teamQueryRequest.getPageSize();
        String sortField = teamQueryRequest.getSortField();
        String sortOrder = teamQueryRequest.getSortOrder();
        // content 需支持模糊搜索
        // 限制爬虫
        if (size > 50) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        QueryWrapper<Team> queryWrapper = new QueryWrapper<>(teamQuery);

        queryWrapper.orderBy(StringUtils.isNotBlank(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC), sortField);
        Page<Team> teamPage = teamService.page(new Page<>(current, size), queryWrapper);
        return ResultUtils.success(teamPage);
    }

    // endregion

}
