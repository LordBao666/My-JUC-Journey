package com.lordbao.threadTools;


import java.util.ArrayDeque;
import java.util.Deque;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Author Lord_Bao
 * @Date 2025/5/30 9:44
 * @Version 1.0
 * <p>
 * 阻塞队列
 */
public class BlockingQueue<T> {

    //任务队列容量
    private final int capacity;

    //任务队列
    private final Deque<T> queue = new ArrayDeque<>();

    //锁
    private final ReentrantLock lock = new ReentrantLock();

    //当容量为空时, 消费线程进入此等待队列
    private final Condition emptyWaitSet = lock.newCondition();
    //当容量为满时, 生产线程进入此等待队列
    private final Condition fullWaitSet = lock.newCondition();

    public BlockingQueue(int capacity) {
        this.capacity = capacity;
    }


    //带等待时间
    public T poll(TimeUnit unit, long timeout) {
        lock.lock();
        try {
            long remainTime = unit.toNanos(timeout);
            //超过等待时间, 返回null. 注意,这里务必添加上 等号. 后同
            if (remainTime <= 0) {
                return null;
            }

            while (queue.size() == 0) {
                try {
                    //更新remainTime
                    remainTime = emptyWaitSet.awaitNanos(remainTime);
                    if (remainTime <= 0) {
                        return null;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T t = queue.removeFirst();
            fullWaitSet.signal();//随机唤醒一个生产者线程
            return t;
        } finally {
            lock.unlock();
        }
    }

    //不带等待时间
    public T poll() {
        lock.lock();
        try {
            //空
            while (queue.size() == 0) {
                try {
                    emptyWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            T t = queue.removeFirst();
            fullWaitSet.signal();//随机唤醒生产者线程
            return t;
        } finally {
            lock.unlock();
        }

    }

    //带等待时间
    public void put(T ele) {

        lock.lock();
        try {
            //满
            while (queue.size() == capacity) {
                try {
                    fullWaitSet.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(ele);
            emptyWaitSet.signal();//随机唤醒一个消费者线程
        } finally {
            lock.unlock();
        }
    }


    //带等待时间
    public boolean put(T ele,TimeUnit unit, long timeout) {
        lock.lock();
        try {
            long remain = unit.toNanos(timeout);
            if(remain<=0){
                return false;
            }

            //满
            while (queue.size() == capacity) {
                try {
                    remain = fullWaitSet.awaitNanos(remain);
                    if(remain<=0){
                        return false;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            queue.addLast(ele);
            emptyWaitSet.signal();//随机唤醒一个消费者线程
            return true;
        } finally {
            lock.unlock();
        }
    }



    public boolean isFull() {
        lock.lock();
        try {
            return capacity==queue.size();
        }finally {
            lock.unlock();
        }
    }
}
