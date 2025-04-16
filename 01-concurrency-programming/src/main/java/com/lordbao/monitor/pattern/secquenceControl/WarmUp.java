package com.lordbao.monitor.pattern.secquenceControl;


import com.lordbao.monitor.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Lord_Bao
 * @Date 2025/4/16 16:18
 * @Version 1.0
 *
 * 线程t1 打印 A, 线程t2 打印B.
 * 要求A 先于  B 打印
 */
@Slf4j
public class WarmUp {

    private static final Object lock = new Object();
    private static  boolean isAOK = false;// 表示A是否打印

    //synchronized 的 wait/notify机制
    public static void method1(){

        Thread t1 = new Thread(() -> {
                synchronized (lock){
                    log.debug("A");
                    isAOK = true;
                    lock.notifyAll();//唤醒等待的所有线程
                }
            },
                "t1");


        Thread t2 = new Thread(() -> {
            synchronized (lock){
                while (!isAOK){
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                log.debug("B");
            }
        },
                "t2");

        t1.start();
        t2.start();
    }


    //park/unpark
    public static void method2() {
        Thread t2 = new Thread(() -> {
                while (!isAOK){
                    LockSupport.park();
                }
                log.debug("B");
            }, "t2");

        Thread t1 = new Thread(() -> {
            log.debug("A");
            isAOK=true;
            LockSupport.unpark(t2);

        }, "t1");

        t1.start();
        t2.start();
    }

    private static final ReentrantLock lock1 = new ReentrantLock();
    private static final Condition AQueue = lock1.newCondition();

    // ReentrantLock
    public static void method3() {
        Thread t1 = new Thread(() -> {
            lock1.lock();
            try {
                log.debug("A");
                isAOK = true;
                AQueue.signalAll();
            }finally {
                lock1.unlock();
            }
        },
                "t1");


        Thread t2 = new Thread(() -> {
            lock1.lock();
            try {
                while (!isAOK){
                    try {
                        AQueue.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();//恢复中断标记
                    }
                }
                log.debug("B");
            }finally {
                lock1.unlock();
            }
        },
                "t2");

        t1.start();
        t2.start();

    }


    public static void main(String[] args) {
//        method1();
//        method2();
        method3();
    }
}
