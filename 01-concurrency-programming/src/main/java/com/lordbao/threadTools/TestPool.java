package com.lordbao.threadTools;


import com.lordbao.threadTools.rejectPolicy.RejectPolicies;
import com.lordbao.threadTools.rejectPolicy.RejectPolicyHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author Lord_Bao
 * @Date 2025/5/30 11:36
 * @Version 1.0
 */
@Slf4j
public class TestPool {

    //下面测试  无限等待的 线程池
    public static void test1(){
        ThreadPool pool = new ThreadPool(2, 2, 1, TimeUnit.SECONDS);
        for (int i = 0; i < 5; i++) {
            int j=i;
            Runnable task = ()->{
                log.debug("{}",j);
            };
            pool.execute(task);
        }
    }

    //下面测试  有限等待的 线程池
    public static void test2(){
        ThreadPool pool = new ThreadPool(2, 2, 4000, TimeUnit.MILLISECONDS);
        for (int i = 0; i < 5; i++) {
            int j=i;
            //睡1s
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            Runnable task = ()->{
                log.debug("{}",j);
            };
            pool.execute(task);
        }
    }


    //下面测试 任务量足够多, 远大于阻塞队列 和 核心线程数的情况
    //下面测试  无限等待的 线程池
    public static void test3(){
        ThreadPool pool = new ThreadPool(2, 2, 1, TimeUnit.SECONDS);
        for (int i = 0; i < 10; i++) {
            int j=i;

            Runnable task = ()->{
                //假设每个任务执行1h...
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}",j);
            };
            pool.execute(task);
        }
    }


    //下面测试  有限等待的 线程池
    public static void test4(){
        long timeOut = 1;
        TimeUnit unit = TimeUnit.SECONDS;
        RejectPolicyHandler policy = RejectPolicies.getWaitPolicy(timeOut,unit);
        ThreadPool pool = new ThreadPool(2, 2, timeOut, unit,policy);
        for (int i = 0; i < 10; i++) {
            int j=i;
            Runnable task = ()->{
                //假设每个任务执行1h...
                try {
                    TimeUnit.HOURS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                log.debug("{}",j);
            };
            pool.execute(task);
        }
    }


    public static void main(String[] args) {

//        test1();
//        test2();
//        test3();
//        test4();
    }
}
