package com.lordbao.memory.pattern;


/**
 * @Author Lord_Bao
 * @Date 2025/4/20 22:28
 * @Version 1.0
 */

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BalkingPattern {
    private Thread thread;

    private boolean isStarted = false;//线程是否已经创建
    private volatile boolean stop = false;

    public void start() {
        synchronized (this) {
            if (isStarted) {
                return;
            }
            isStarted = true;
        }
        thread = new Thread(() -> {
            while (true) {
                Thread current = Thread.currentThread();
                if (stop) {
                    //处理后事
                    log.debug("over...");
                    break;
                } else {
                    //监控核心业务
                    try {
                        Thread.sleep(1000);
                        log.debug("watch....");
                    } catch (InterruptedException e) {

                    }
                }
            }
        });
        thread.start();
    }

    public void stop() {
        stop = true;
        thread.interrupt();
    }

//    public static void main(String[] args) {
//        BalkingPattern bp = new BalkingPattern();
//        for (int i = 0; i < 10; i++) {
//            new Thread(() -> {
//                bp.start();
//            }).start();
//        }
//    }
}
