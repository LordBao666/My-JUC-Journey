package com.lordbao.utils;


import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Lord_Bao
 * @Date 2025/4/16 11:19
 * @Version 1.0
 */
@Slf4j
public class ReentrantLockHelper {
    /**
     *
     * @param runnable 核心业务代码
     * @param successMessage 抢夺锁成功的日志信息,如果为null,表示不打印日志
     * @param failureMessage 抢夺锁失败的日志信息,如果为null,表示不打印日志
     */
    public static void tryLock(ReentrantLock lock, long timeout,
                               TimeUnit timeUnit,
                               Runnable runnable,
                               String successMessage,
                               String failureMessage
                               ){
        try {
            if(lock.tryLock(timeout,timeUnit)){
                  try {
                      if(successMessage!=null){
                          log.debug(successMessage);
                      }
                      if(runnable!=null){
                          try {
                              runnable.run();//执行核心业务
                          } catch (Exception e) {
                              log.error("任务执行异常", e);
                          }
                      }
                  }finally {
                      lock.unlock();
                  }
            }else {
                if(failureMessage!=null){
                    log.debug(failureMessage);
                }
            }
        }catch (InterruptedException e){
            log.error("锁等待被中断", e);
            Thread.currentThread().interrupt();//恢复中断标记
        }

    }
}
