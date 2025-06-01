package com.lordbao.threadTools.rejectPolicy;

import com.lordbao.threadTools.BlockingQueue;
import com.lordbao.threadTools.ThreadPool;

@FunctionalInterface
public interface RejectPolicyHandler {
    public void reject(ThreadPool pool, Runnable task);
}