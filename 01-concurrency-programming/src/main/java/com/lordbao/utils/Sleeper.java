package com.lordbao.utils;


/**
 * @Author Lord_Bao
 * @Date 2025/4/9 16:12
 * @Version 1.0
 */
public class Sleeper {
    public static void sleep(long mills){
        try {
            Thread.sleep(mills);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
