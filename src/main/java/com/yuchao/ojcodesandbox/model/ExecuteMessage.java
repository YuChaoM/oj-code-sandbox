package com.yuchao.ojcodesandbox.model;

import lombok.Data;

/**
 * 进程执行信息
 * @author 蒙宇潮
 * @create 2023-09-11  10:55
 */
@Data
public class ExecuteMessage {

    private Integer exitValue;

    private String message;

    private String errorMessage;

    private Long time;

}
