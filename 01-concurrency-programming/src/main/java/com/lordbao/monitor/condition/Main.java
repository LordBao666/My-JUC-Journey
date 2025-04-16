package com.lordbao.monitor.condition;


import com.lordbao.monitor.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Lord_Bao
 * @Date 2025/4/16 14:52
 * @Version 1.
 *
 * 基于ReentrantLock 和 Condition 的 await+signal/signalALl机制
 * 相比于 synchronized的wait+notify/notifyAll 机制, 前者对不同的条件划分得更加精细
 */
@Slf4j
public class Main {
    private static final ReentrantLock lock = new ReentrantLock();
    private static Condition waitCigaretteQueue = lock.newCondition();
    private static Condition waitbreakfastQueue = lock.newCondition();
    private static volatile boolean hasCigrette = false;
    private static volatile boolean hasBreakfast = false;


    public static void main(String[] args) {

        new Thread(() -> {
            try {
                lock.lock();
                while (!hasCigrette) {
                    try {
                        waitCigaretteQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("等到了它的烟");
            } finally {
                lock.unlock();
            }
        }).start();


        new Thread(() -> {
            try {
                lock.lock();
                while (!hasBreakfast) {
                    try {
                        waitbreakfastQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                log.debug("等到了它的早餐");
            } finally {
                lock.unlock();
            }
        }).start();


        Sleeper.sleep(1000);
        sendBreakfast();
        Sleeper.sleep(1000);
        sendCigarette();
    }


    private static void sendCigarette() {
        lock.lock();
        try {
            log.debug("送烟来了");
            hasCigrette = true;
            waitCigaretteQueue.signal();
        } finally {
            lock.unlock();
        }
    }

    private static void sendBreakfast() {
        lock.lock();
        try {
            log.debug("送早餐来了");
            hasBreakfast = true;
            waitbreakfastQueue.signal();
        } finally {
            lock.unlock();
        }
    }

}
