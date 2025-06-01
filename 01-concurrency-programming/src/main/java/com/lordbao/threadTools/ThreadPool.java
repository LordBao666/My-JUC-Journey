package com.lordbao.threadTools;


import com.lordbao.threadTools.rejectPolicy.RejectPolicies;
import com.lordbao.threadTools.rejectPolicy.RejectPolicyHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * @Author Lord_Bao
 * @Date 2025/5/30 10:40
 * @Version 1.0
 */
@Slf4j
public class ThreadPool {


    private final BlockingQueue<Runnable> blockingQueue;
    private final int queueCapacity;//阻塞队列大小

    private final int coreSize; //核心线程数目
    private final HashSet<Worker> workers;

    //获取任务的等待时间
    private final TimeUnit unit;
    private final long timeout;
    private final RejectPolicyHandler rejectPolicyHandler;


    public ThreadPool(int queueCapacity, int coreSize, long timeout, TimeUnit unit) {
        //默认是无限等待策略
        this(queueCapacity,coreSize,timeout,unit, RejectPolicies.WAIT_FOREVER);
    }

    public ThreadPool(int queueCapacity, int coreSize, long timeout, TimeUnit unit, RejectPolicyHandler rejectPolicyHandler) {
        this.queueCapacity = queueCapacity;
        this.coreSize = coreSize;
        this.timeout = timeout;
        this.unit = unit;

        workers=new HashSet<>();
        blockingQueue=new BlockingQueue<>(queueCapacity);
        this.rejectPolicyHandler = rejectPolicyHandler;
    }

    //执行任务
    public void execute(Runnable task){
        synchronized (workers){
            //核心线程数够用
            if(workers.size()< coreSize){
                Worker worker = new Worker(task);
                workers.add(worker);
                worker.start();
                log.debug("生成核心线程,处理任务...");
            }else if(!blockingQueue.isFull()){
                log.debug("进入阻塞队列...");
                blockingQueue.put(task);

            }else {
                log.debug("执行拒绝策略...");
                rejectPolicyHandler.reject(this,task);
            }
        }
    }

    public BlockingQueue<Runnable> getBlockingQueue() {
        return blockingQueue;
    }






    class Worker extends Thread{

        private Runnable task ;
        public Worker(Runnable task) {
            this.task= task;
        }

        //Worker会处理task,将task 处理完毕后,还会检查阻塞队列是否有任务
        //如果有还会继续执行 阻塞队列的任务.当 阻塞队列没有任务时,才销毁线程.
        @Override
        public void run() {
            //先处理自己的任务
            if(task!=null){
                task.run();
            }

            //处理阻塞队列的剩余任务
//            while ((task= blockingQueue.poll())!=null){
            while ((task= blockingQueue.poll(unit,timeout))!=null){
                task.run();
            }

            //任务处理完毕...
            synchronized (workers){
                workers.remove(this);//清除当前线程
            }
        }
    }
}
