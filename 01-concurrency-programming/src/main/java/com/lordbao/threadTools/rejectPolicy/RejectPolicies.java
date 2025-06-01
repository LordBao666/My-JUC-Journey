package com.lordbao.threadTools.rejectPolicy;


import com.lordbao.threadTools.BlockingQueue;
import com.lordbao.threadTools.ThreadPool;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @Author Lord_Bao
 * @Date 2025/5/30 15:51
 * @Version 1.0
 */
@Slf4j
public class RejectPolicies {
    private RejectPolicies(){
    }

    public static final RejectPolicyHandler WAIT_FOREVER = new WaitForeverPolicy();
    public static final RejectPolicyHandler DISCARD = new DisCardPolicy();
    public static final RejectPolicyHandler THROW_EXCEPTION = new ThrowExceptionPolicy();
    public static final RejectPolicyHandler DO_IT_BY_CALLER = new DoItByCallerPolicy();

    public static RejectPolicyHandler getWaitPolicy(long timeOut,TimeUnit timeUnit){
        return  new WaitPolicy(timeOut,timeUnit);
    }



    //无限等待策略
    private static class WaitForeverPolicy implements RejectPolicyHandler {

        @Override
        public void reject(ThreadPool pool, Runnable task) {
            BlockingQueue<Runnable> queue = pool.getBlockingQueue();
            queue.put(task);
        }
    }

    //等待策略
    private static class WaitPolicy implements RejectPolicyHandler{
        private final long timeout;
        private final TimeUnit timeUnit;

        public WaitPolicy(long timeout, TimeUnit timeUnit) {
            this.timeout = timeout;
            this.timeUnit = timeUnit;
        }

        @Override
        public void reject(ThreadPool pool, Runnable task) {
            BlockingQueue<Runnable> queue = pool.getBlockingQueue();
            queue.put(task,timeUnit,timeout);
        }
    }


    //丢弃策略
    private static class DisCardPolicy  implements  RejectPolicyHandler{
        @Override
        public void reject(ThreadPool pool, Runnable task) {
            //什么也不做...
        }
    }


    //抛出异常策略
    private static class ThrowExceptionPolicy implements RejectPolicyHandler{
        @Override
        public void reject(ThreadPool pool, Runnable task) {
            throw new IllegalStateException("ThreadPool  is  FULL!!!");
        }
    }

    //调用者自己执行策略
    private static class DoItByCallerPolicy implements RejectPolicyHandler{
        @Override
        public void reject(ThreadPool pool, Runnable task) {
            task.run();
        }
    }



}
