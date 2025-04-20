package com.lordbao.monitor.problem;


import com.lordbao.utils.Sleeper;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author Lord_Bao
 * @Date 2025/4/14 17:40
 * @Version 1.0
 */
@Slf4j(topic = "c.livelock")
public class LiveLock {
    static volatile int count = 10;
    public static void main(String[] args) {
        new Thread(() -> {
            // 期望减到 0 退出循环
            while (count > 0) {
                Sleeper.sleep(200);
                count--;
                log.info("count: {}", count);
            }
        },
                "t1").start();
        new Thread(() -> {
            // 期望超过 20 退出循环
            while (count < 20) {
                Sleeper.sleep(200);
                count++;
                log.info("count: {}", count);
            }
        },
                "t2").start();
    }
}
