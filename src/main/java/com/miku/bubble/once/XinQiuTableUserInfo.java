package com.miku.bubble.once;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

@Data
public class XinQiuTableUserInfo {
    /**
     * id
     */
    @ExcelProperty("成员编号")
    private String planetCode;
    /**
     * 用户名
     */
    @ExcelProperty("用户昵称")
    private String userName;
}
