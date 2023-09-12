package com.yuchao.ojcodesandbox;

import com.yuchao.ojcodesandbox.model.ExecuteCodeRequest;
import com.yuchao.ojcodesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 * @author 蒙宇潮
 * @create 2023-09-08  21:07
 */
public interface CodeSandBox {

    /**
     * 执行代码
     * @param executeCodeRequest
     * @return ExecuteCodeResponse
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);

}
