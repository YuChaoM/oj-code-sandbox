package com.yuchao.ojcodesandbox.common;

/**
 * @author 蒙宇潮
 * @create 2023-09-13  9:08
 */
public enum StatusCode {
    SUCCEED(0,"执行完成"),
    SANDBOX_ERROR(1,"代码沙箱错误"),
    USER_CODE_ERROR(2, "用户代码错误"),
    COMPILE(3, "编译失败");


    /**
     * 状态码
     */
    private final int code;

    /**
     * 信息
     */
    private final String message;

    StatusCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
