package com.yuchao.ojcodesandbox.utils;

import com.yuchao.ojcodesandbox.model.ExecuteMessage;
import org.springframework.util.StopWatch;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author 蒙宇潮
 * @create 2023-09-11  10:54
 */
public class ProcessUtils {

    /**
     * 执行进程并获取信息
     *
     * @param runProcess
     * @param opName
     * @return
     */
    public static ExecuteMessage runProcessAndGetMessage(Process runProcess, String opName) {
        ExecuteMessage executeMessage = new ExecuteMessage();
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            // 等待程序执行，获取状态码
            int exitValue = runProcess.waitFor();
            executeMessage.setExitValue(exitValue);
            if (exitValue == 0) {
                System.out.println(opName + "成功");
                // 获取正常的输出信息
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(compileOutputLine);
                }
                executeMessage.setMessage(stringBuilder.toString());
            } else {
                // 异常退出
                System.out.println(opName + "失败，错误码：" + exitValue);
                // 获取正常的输出信息
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(runProcess.getInputStream()));
                StringBuilder stringBuilder = new StringBuilder();
                String compileOutputLine;
                while ((compileOutputLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(compileOutputLine);
                }
                executeMessage.setMessage(stringBuilder.toString());
                // 获取错误的输出信息
                BufferedReader errorBufferedReader = new BufferedReader(new InputStreamReader(runProcess.getErrorStream()));
                StringBuilder errorStringBuilder = new StringBuilder();
                String errorCompileOutputLine;
                while ((errorCompileOutputLine = errorBufferedReader.readLine()) != null) {
                    errorStringBuilder.append(errorCompileOutputLine);
                }
                executeMessage.setErrorMessage(errorStringBuilder.toString());
            }
            stopWatch.stop();
            executeMessage.setTime(stopWatch.getLastTaskTimeMillis());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return executeMessage;
    }


}
