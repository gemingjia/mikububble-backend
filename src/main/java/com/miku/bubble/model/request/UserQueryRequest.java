package com.miku.bubble.model.request;

import com.miku.bubble.common.PageRequest;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 用户注册请求体
 *
 * @author Ge Mingjia
 */
@Data
public class UserQueryRequest  extends PageRequest{

    /**
     * id
     */
    private long id;

    /**
     * 用户昵称
     */
    private String username;


    /**
     * 性别
     */
    private Integer gender;

    /**
     * 电话
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 标签列表 json
     */
    private String tags;

    /**
     * 状态 0 - 正常
     */
    private Integer userStatus;


    /**
     * 是否删除
     */
    private Integer isDelete;

    /**
     * 用户角色 0 - 普通用户 1 - 管理员
     */
    private Integer userRole;

    /**
     * 星球编号
     */
    private String planetCode;
}
