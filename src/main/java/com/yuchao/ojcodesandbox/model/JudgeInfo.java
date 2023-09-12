package com.yuchao.ojcodesandbox.model;

import lombok.Data;

/**
 * @author 蒙宇潮
 * @create 2023-09-06  10:00
 */
@Data
public class JudgeInfo {

    /**
     * 程序执行信息
     */
    private String message;

    /**
     * 消耗内存
     */
    private Long memory;

    /**
     * 消耗时间
     */
    private Long time;
}
