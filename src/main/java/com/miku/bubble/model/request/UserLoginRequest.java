package com.miku.bubble.model.request;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户登录请求体
 *
 * @author Ge Mingjia
 */
@Data
public class UserLoginRequest implements Serializable {

    private static final long serialVersionUID = 3191241716373120793L;

    private String userAccount;

    private String userPassword;
}
