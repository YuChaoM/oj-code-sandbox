package com.yuchao.ojcodesandbox.security;

/**
 * @author 蒙宇潮
 * @create 2023-09-11  17:25
 */

import cn.hutool.core.io.FileUtil;

import java.nio.charset.Charset;

/**
 * 测试安全管理器
 */
public class TestSecurityManager {

    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());
        FileUtil.writeString("aa", "aaa", Charset.defaultCharset());
    }
}

