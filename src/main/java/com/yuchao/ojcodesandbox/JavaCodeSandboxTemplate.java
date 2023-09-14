package com.yuchao.ojcodesandbox;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.yuchao.ojcodesandbox.common.StatusCode;
import com.yuchao.ojcodesandbox.model.ExecuteCodeRequest;
import com.yuchao.ojcodesandbox.model.ExecuteCodeResponse;
import com.yuchao.ojcodesandbox.model.ExecuteMessage;
import com.yuchao.ojcodesandbox.model.JudgeInfo;
import com.yuchao.ojcodesandbox.utils.ProcessUtils;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Java代码沙箱模版方法的实现
 *
 * @author yuchao
 * @create 2023-09-13  8:35
 */
public abstract class JavaCodeSandboxTemplate implements CodeSandBox {

    public static final String GLOBAL_JAVA_CODE_NAME = "tmpCode";
    private static final String GLOBAL_JAVA_CLASS_NAME = "Main.java";
    private static final long TIME_OUT = 5000L;

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        String code = executeCodeRequest.getCode();
        List<String> inputList = executeCodeRequest.getInputList();
        String language = executeCodeRequest.getLanguage();

        // 1.把用户的代码保存为文件
        File userCodeFile = saveCodeFile(code);
        // 2.编译代码，得到class文件
        ExecuteMessage compileExecuteMessage = compileFile(userCodeFile);
        System.out.println("编译信息"+compileExecuteMessage);
        if (compileExecuteMessage.getExitValue() != 0) {
            removeFile(userCodeFile);
            return getErrorResponse(compileExecuteMessage);
        }
        //3.执行代码，得到输出结果,每个用例都要执行
        List<ExecuteMessage> executeMessageList = runFile(userCodeFile, inputList);
        // 4.收集整理输出结果
        ExecuteCodeResponse executeCodeResponse = getOutputResponse(executeMessageList);
        // 5.文件清理，释放空间
        removeFile(userCodeFile);
        return executeCodeResponse;
    }

    /**
     * 1.把用户代码保存文件
     *
     * @param code 用户代码
     * @return
     */
    public File saveCodeFile(String code) {
        String userDir = System.getProperty("user.dir");// 获取项目的根路径
        String globalCodePathName = userDir + File.separator + GLOBAL_JAVA_CODE_NAME;// 用户代码保存的目录
        if (!FileUtil.exist(globalCodePathName)) {
            FileUtil.mkdir(globalCodePathName);
        }
        // 把不同用户之间的代码隔离存放
        String userCodeParentPath = globalCodePathName + File.separator + UUID.randomUUID();
        String userCodePath = userCodeParentPath + File.separator + GLOBAL_JAVA_CLASS_NAME;
        File userCodeFile = FileUtil.writeString(code, userCodePath, StandardCharsets.UTF_8);//把代码写入到文件
        return userCodeFile;
    }

    /**
     * 2.编译代码
     *
     * @param userCodeFile
     * @return
     */
    public ExecuteMessage compileFile(File userCodeFile) {
        String compiledCmd = String.format("javac -encoding utf-8 %s", userCodeFile.getAbsolutePath());
        try {
            Process compileProcess = Runtime.getRuntime().exec(compiledCmd);
            ExecuteMessage compileExecuteMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            Integer exitValue = compileExecuteMessage.getExitValue();
            compileExecuteMessage.setExitValue(exitValue);
            if (exitValue != 0) {
                compileExecuteMessage.setErrorMessage("编译错误");
                return compileExecuteMessage;
            }
            compileExecuteMessage.setMessage("编译成功");
            return compileExecuteMessage;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 3.执行代码，得到输出结果
     *
     * @param userCodeFile 用户代码文件
     * @param inputList    测试用例
     * @return
     */
    public List<ExecuteMessage> runFile(File userCodeFile, List<String> inputList) {
        String userCodeParentPath = userCodeFile.getParentFile().getAbsolutePath();
        ArrayList<ExecuteMessage> executeMessageList = new ArrayList<>();
        for (String input : inputList) {
            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s Main %s", userCodeParentPath, input);
//            String runCmd = String.format("java -Xmx256m -Dfile.encoding=UTF-8 -cp %s;%s -Djava.security.manager=%s Main %s", userCodeParentPath, SECURITY_MANAGER_PATH, SECURITY_MANAGER_CLASS_NAME, input);
            try {
                Process runProcess = Runtime.getRuntime().exec(runCmd);
                // 超时控制,子线程启动后，主线程继续执行
                new Thread(() -> {
                    try {
                        Thread.sleep(TIME_OUT);
                        System.out.println("超时，中断");
                        runProcess.destroy();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                System.out.println(executeMessage);
                executeMessageList.add(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException("执行错误", e);
            }
        }
        return executeMessageList;
    }

    /**
     * 4.收集整理输出结果
     *
     * @param executeMessageList 每个测试用例的执行信息
     * @return
     */
    public ExecuteCodeResponse getOutputResponse(List<ExecuteMessage> executeMessageList) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        ArrayList<String> outputList = new ArrayList<>();
        long maxTime = 0;
        for (ExecuteMessage executeMessage : executeMessageList) {
            String errorMessage = executeMessage.getErrorMessage();
            if (StrUtil.isNotBlank(errorMessage)) {
                executeCodeResponse.setMessage(errorMessage);
                // 用户提交的代码执行中存在异常
                executeCodeResponse.setStatus(StatusCode.USER_CODE_ERROR.getCode());
                break;
            }
            Long time = executeMessage.getTime();
            // message记录的是程序的输出
            outputList.add(executeMessage.getMessage());
            if (time != null) {
                maxTime = Math.max(maxTime, time);
            }
        }
        // 正常运行完成
        if (outputList.size() == executeMessageList.size()) {
            executeCodeResponse.setStatus(StatusCode.SUCCEED.getCode());
        }
        executeCodeResponse.setOutputList(outputList);
        executeCodeResponse.setMessage(StatusCode.SUCCEED.getMessage());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setTime(maxTime);
        // 要借助第三方库来获取内存占用，非常麻烦，此处不做实现
//        judgeInfo.setMemory();
        // todo judgeInfo的message这里先不设置，因为最后返回的是一个是否通过
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;
    }

    /**
     * 5.文件清理，释放空间
     *
     * @param userCodeFile
     * @return
     */
    public boolean removeFile(File userCodeFile) {
        if (userCodeFile.getParentFile() != null) {
            File userCodeParentPath = userCodeFile.getParentFile();
            System.out.println("用户代码目录" + userCodeParentPath);
            boolean del = FileUtil.del(userCodeParentPath);
            System.out.println("删除" + (del ? "成功" : "失败"));
            return del;
        }
        return true;
    }

    /**
     * 6.获取错误响应
     *
     * @param executeMessage
     * @return
     */
    private ExecuteCodeResponse getErrorResponse(ExecuteMessage executeMessage) {
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(new ArrayList<>());
        executeCodeResponse.setMessage(executeMessage.getErrorMessage());
        executeCodeResponse.setStatus(executeMessage.getExitValue());
        executeCodeResponse.setJudgeInfo(new JudgeInfo());
        return executeCodeResponse;
    }
}
