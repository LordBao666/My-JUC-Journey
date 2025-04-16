package com.lordbao.monitor.problem;


import com.lordbao.monitor.utils.ReentrantLockHelper;
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
public class DeadLockSolve2 {

    public static void main(String[] args) {
        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();
        final int timeout = 5;//等待锁时长为5s

        Thread t1 = new Thread(() -> {
            ReentrantLockHelper.tryLock(lock1, timeout, TimeUnit.SECONDS,
                    () -> {
                        Sleeper.sleep(1000);
                        ReentrantLockHelper.tryLock(lock2, timeout, TimeUnit.SECONDS, () -> {
                            log.debug("working...");
                        }, "抢夺锁2成功", "抢夺锁2失败");
                    }, "抢夺锁1成功", "抢夺锁1失败"
            );
        }, "t1");


        Thread t2 = new Thread(() -> {
            ReentrantLockHelper.tryLock(lock2, timeout, TimeUnit.SECONDS,
                    () -> {
                        Sleeper.sleep(1000);
                        ReentrantLockHelper.tryLock(lock1, timeout, TimeUnit.SECONDS, () -> {
                            log.debug("working...");
                        }, "抢夺锁1成功", "抢夺锁1失败");
                    }, "抢夺锁2成功", "抢夺锁2失败"
            );
        }, "t2");


        t1.start();
        t2.start();
    }


}


