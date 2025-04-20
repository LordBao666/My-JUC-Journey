package com.lordbao.memory.pattern;


import com.lordbao.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Lord_Bao
 * @Date 2025/4/20 21:56
 * @Version 1.0
 *
 * 利用volatile 完成协作式线程终止
 */
@Slf4j
public class Monitor {
    private Thread thread;
    private boolean stop = false;

    public void start() {
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
        thread.interrupt();//此处是为了避免sleep()
    }


    public static void main(String[] args) {
        Monitor monitor = new Monitor();
        monitor.start();

        Sleeper.sleep(5000);
        monitor.stop();
    }
}
