package com.yuchao.ojcodesandbox.unsafe;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author 蒙宇潮
 * @create 2023-09-11  15:44
 */
public class WriteFileError {

    public static void main(String[] args) throws IOException {
        String userDir = System.getProperty("user.dir");
        String filePath = userDir + File.separator + "src/main/resources/木马程序.bat";
        String errorProgram = "java -version 2>&1"; // 2>是用来重定向标准错误,&1表示的就是标准输出
        Files.write(Paths.get(filePath), Arrays.asList(errorProgram));
        System.out.println("木马程序成功写入");

    }
}
