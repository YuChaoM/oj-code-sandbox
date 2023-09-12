package com.yuchao.ojcodesandbox.unsafe;

/**
 * @author 蒙宇潮
 * @create 2023-09-11  15:34
 */
public class SleepError {

    public static void main(String[] args) throws InterruptedException {
        long ONE_HOUR = 60 * 60 * 1000L;
        Thread.sleep(ONE_HOUR);
        System.out.println("睡醒了");
    }
}
