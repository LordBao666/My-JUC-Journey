package com.lordbao.monitor.pattern.secquenceControl;


import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Lord_Bao
 * @Date 2025/4/16 16:56
 * @Version 1.0
 * <p>
 * 线程 1 输出 a 5 次，线程 2 输出 b 5 次，线程 3 输出 c 5 次。现在要求输出 abcabcabcabcabc 怎么实现。
 */
@Slf4j
public class Exercise {


    //synchronized方案
    public static void method1() {
        MyLock myLock = new MyLock(0);
        Thread t1 = new Thread(new MyRun(0, 1, "A", 5, myLock), "t1");
        Thread t2 = new Thread(new MyRun(1, 2, "B", 5, myLock), "t2");
        Thread t3 = new Thread(new MyRun(2, 0, "C", 5, myLock), "t3");

        t1.start();
        t2.start();
        t3.start();
    }

    private static class MyLock {
        private int id;//当前允许运行线程id;

        public MyLock(int id) {
            this.id = id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private static class MyRun implements Runnable {
        private int id;//当前线程id
        private int nextId;//下个线程运行id
        private String message;//打印的信息
        private int count;//打印次数
        private final MyLock lock;//锁对象

        public MyRun(int id, int nextId, String message, int count, MyLock lock) {
            this.id = id;
            this.nextId = nextId;
            this.message = message;
            this.count = count;
            this.lock = lock;//锁对象
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                synchronized (lock) {
                    while (lock.getId() != id) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();//恢复中断标记
                        }
                    }
                    log.debug(message);
                    lock.setId(nextId);
                    lock.notifyAll();
                }
            }
        }
    }


//...................分割线.........................

    //ReentrantLock方案(多Condition)
    public static void method2() {
        ReentrantLock lock = new ReentrantLock();
        Condition A = lock.newCondition();
        Condition B = lock.newCondition();
        Condition C = lock.newCondition();
        HashMap<Integer, Condition> map = new HashMap<>();
        map.put(0, A);
        map.put(1, B);
        map.put(2, C);
        MyLock2 myLock = new MyLock2(0, lock, map);


        Thread t1 = new Thread(new MyRun2(0, 1, "A", 5, myLock), "t1");
        Thread t2 = new Thread(new MyRun2(1, 2, "B", 5, myLock), "t2");
        Thread t3 = new Thread(new MyRun2(2, 0, "C", 5, myLock), "t3");


        t1.start();
        t2.start();
        t3.start();
    }

    private static class MyLock2 {
        private ReentrantLock lock;
        private int id;
        private Map<Integer, Condition> map; //id-->Condition

        public MyLock2(int id, ReentrantLock lock, Map<Integer, Condition> map) {
            this.id = id;
            this.lock = lock;
            this.map = map;
        }

        public Condition getQueue(Integer id) {
            return map.get(id);
        }

        public int getId() {
            return id;
        }

        public ReentrantLock getLock() {
            return lock;
        }

        public void setId(int id) {
            this.id = id;
        }

    }

    private static class MyRun2 implements Runnable {
        private int id;//当前线程id
        private int nextId;//下个线程运行id
        private String message;//打印的信息
        private int count;//打印次数
        private final MyLock2 lock;//锁对象

        public MyRun2(int id, int nextId, String message, int count, MyLock2 lock) {
            this.id = id;
            this.nextId = nextId;
            this.message = message;
            this.count = count;
            this.lock = lock;//锁对象
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                lock.getLock().lock();
                try {
                    while (lock.getId() != id) {
                        try {
                            lock.getQueue(id).await();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();//恢复中断标记
                        }
                    }
                    log.debug(message);
                    lock.setId(nextId);
                    lock.getQueue(nextId).signalAll();
                } finally {
                    lock.getLock().unlock();
                }

            }
        }
    }


    //...................分割线.........................


    private static class MyLock3{
        private int id;//当前允许运行线程id;

        public MyLock3(int id) {
            this.id = id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return id;
        }
    }

    private static class MyRun3 implements Runnable {
        private int id;//当前线程id
        private int nextId;//下个线程运行id
        private String message;//打印的信息
        private int count;//打印次数
        private final MyLock3 lock;//锁对象

        public MyRun3(int id, int nextId, String message, int count, MyLock3 lock) {
            this.id = id;
            this.nextId = nextId;
            this.message = message;
            this.count = count;
            this.lock = lock;//锁对象
        }

        @Override
        public void run() {
            for (int i = 0; i < count; i++) {
                LockSupport.park();
            }
        }
    }


    public static void main(String[] args) {
//        method1();
        method2();
    }
}
