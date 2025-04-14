package com.lordbao.monitor.problem;


/**
 * @Author Lord_Bao
 * @Date 2025/4/14 17:08
 * @Version 1.0
 */

import com.lordbao.monitor.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

@Slf4j(topic = "c.deadlock")
public class DeadLock {

    public static void main(String[] args) {
        Object lock1 = new Object();
        Object lock2 = new Object();


        Thread t1 = new Thread(()->{
            synchronized (lock1){
                Sleeper.sleep(1000);
                synchronized (lock2){
                    System.out.println("working....");
                }
            }
        },"t1");

        Thread t2 = new Thread(()->{
            synchronized (lock2){
                Sleeper.sleep(1000);
                synchronized (lock1){
                    System.out.println("working....");
                }
            }
        },"t2");


        t1.start();
        t2.start();
    }


}
