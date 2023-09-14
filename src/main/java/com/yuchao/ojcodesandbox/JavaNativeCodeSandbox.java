package com.yuchao.ojcodesandbox;

import com.yuchao.ojcodesandbox.model.ExecuteCodeRequest;
import com.yuchao.ojcodesandbox.model.ExecuteCodeResponse;
import org.springframework.stereotype.Component;

/**
 * Java原生代码沙箱（直接复用模板方法）
 * @author yuchao
 * @create 2023-09-11  10:13
 */
@Component
public class JavaNativeCodeSandbox extends JavaCodeSandboxTemplate {

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        return super.executeCode(executeCodeRequest);
    }
}
