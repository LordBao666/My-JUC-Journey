package com.lordbao.monitor.problem;


import com.lordbao.monitor.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Lord_Bao
 * @Date 2025/4/16 11:13
 * @Version 1.0
 */
@Slf4j
public class DeadLockSolve {

    public static void main(String[] args) {
        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        final int timeout=5;//等待锁时长为5s

        Thread t1 = new Thread(()->{
            try {
                if(lock1.tryLock(5, TimeUnit.SECONDS)){
                    try {
                        try {
                            log.debug("获取锁1成功");
                            Sleeper.sleep(1000);
                            if(lock2.tryLock(5,TimeUnit.SECONDS)){
                                try {
                                    log.debug("获取锁2成功");
                                    log.debug("working...");
                                }finally {
                                    lock2.unlock();
                                }
                            }else {
                                log.debug("获取锁2失败");
                            }
                        }catch (InterruptedException e) {
                            log.error("锁等待被中断", e);
                            Thread.currentThread().interrupt();//中断标记恢复
                        }
                    }finally {
                        lock1.unlock();
                    }
                }else {
                    log.debug("获取锁1失败");
                }
            } catch (InterruptedException e) {
                log.error("锁等待被中断", e);
                Thread.currentThread().interrupt();//中断标记恢复
            }
        },"t1");

        Thread t2 = new Thread(()->{
            try {
                if(lock2.tryLock(5, TimeUnit.SECONDS)){
                    try {
                        try {
                            log.debug("获取锁2成功");
                            Sleeper.sleep(1000);
                            if(lock1.tryLock(5,TimeUnit.SECONDS)){
                                try {
                                    log.debug("获取锁1成功");
                                    log.debug("working...");
                                }finally {
                                    lock1.unlock();
                                }
                            }else {
                                log.debug("获取锁1失败");
                            }
                        }catch (InterruptedException e) {
                            log.error("锁等待被中断", e);
                            Thread.currentThread().interrupt();//中断标记恢复
                        }
                    }finally {
                        lock2.unlock();
                    }
                }else {
                    log.debug("获取锁2失败");
                }
            } catch (InterruptedException e) {
                log.error("锁等待被中断", e);
                Thread.currentThread().interrupt();//中断标记恢复
            }
        },"t2");


        t1.start();
        t2.start();
    }


}


